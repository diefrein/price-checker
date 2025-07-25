package ru.diefrein.pricechecker.storage.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.storage.dto.Page;
import ru.diefrein.pricechecker.storage.dto.PageRequest;
import ru.diefrein.pricechecker.storage.entity.Product;
import ru.diefrein.pricechecker.storage.entity.User;
import ru.diefrein.pricechecker.storage.exception.EntityNotFoundException;
import ru.diefrein.pricechecker.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.storage.repository.ProductRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductRepositoryImpl implements ProductRepository {

    private static final String INSERT_PRODUCT_STATEMENT = """
            INSERT INTO checker.products (user_id, link, name, actual_price, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_PRODUCT_HISTORY_STATEMENT = """
            INSERT INTO checker.products_history (product_id, price, price_at_date_time) VALUES (?, ?, ?)
            """;
    private static final String SELECT_PRODUCT_BY_USER_ID_STATEMENT = """
            SELECT * FROM checker.products WHERE user_id = ?
            """;
    private static final String SELECT_PRODUCT_BY_USER_ID_PAGE_STATEMENT = """
            SELECT * FROM checker.products WHERE user_id = ? limit ? offset ?
            """;
    private static final String SELECT_PRODUCT_BY_ID_STATEMENT = """
            SELECT * FROM checker.products WHERE id = ?
            """;
    private static final String DELETE_PRODUCT_BY_ID_STATEMENT = """
            DELETE FROM checker.products WHERE id = ?
            """;
    private static final String DELETE_PRODUCT_HISTORY_BY_PRODUCT_ID_STATEMENT = """
            DELETE FROM checker.products_history WHERE product_id = ?
            """;
    private static final String UPDATE_PRODUCT_BY_ID = """
            UPDATE checker.products SET name = ?, actual_price = ?, updated_at = ? WHERE id = ?
            """;

    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);
    private final DataSource dataSource;

    public ProductRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Product create(UUID userId,
                          String link,
                          String name,
                          Double actualPrice) {
        LocalDateTime now = LocalDateTime.now();
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            Product product;
            try {
                try (PreparedStatement insertProductStmt =
                             conn.prepareStatement(INSERT_PRODUCT_STATEMENT, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement insertProductHistoryStmt =
                             conn.prepareStatement(INSERT_PRODUCT_HISTORY_STATEMENT)
                ) {
                    insertProductStmt.setObject(1, userId);
                    insertProductStmt.setString(2, link);
                    insertProductStmt.setString(3, name);
                    insertProductStmt.setDouble(4, actualPrice);
                    insertProductStmt.setTimestamp(5, Timestamp.valueOf(now));
                    insertProductStmt.setTimestamp(6, Timestamp.valueOf(now));
                    insertProductStmt.executeUpdate();

                    UUID productId;
                    try (ResultSet generatedKeys = insertProductStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            productId = (UUID) generatedKeys.getObject(1);
                        } else {
                            throw new SQLException("Creating user failed, no ID obtained");
                        }
                    }

                    try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT_BY_ID_STATEMENT)) {
                        stmt.setObject(1, productId);
                        ResultSet rs = stmt.executeQuery();
                        if (!rs.next()) {
                            throw new EntityNotFoundException(String.format("Product with id=%s not found", productId));
                        }
                        product = map(rs);
                    }

                    insertProductHistoryStmt.setObject(1, product.id());
                    insertProductHistoryStmt.setDouble(2, actualPrice);
                    insertProductHistoryStmt.setTimestamp(3, Timestamp.valueOf(now));
                    insertProductHistoryStmt.execute();
                }
                conn.commit();
                return product;
            } catch (Exception e) {
                conn.rollback();
                log.error("Transaction rolled back due to exception", e);
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection conn,
                       UUID productId,
                       String name,
                       Double actualPrice) {
        LocalDateTime now = LocalDateTime.now();
        try (PreparedStatement updateProductStmt = conn.prepareStatement(UPDATE_PRODUCT_BY_ID);
             PreparedStatement insertProductHistoryStmt = conn.prepareStatement(INSERT_PRODUCT_HISTORY_STATEMENT)) {
            updateProductStmt.setString(1, name);
            updateProductStmt.setDouble(2, actualPrice);
            updateProductStmt.setTimestamp(3, Timestamp.valueOf(now));
            updateProductStmt.setObject(4, productId);
            updateProductStmt.executeUpdate();

            insertProductHistoryStmt.setObject(1, productId);
            insertProductHistoryStmt.setDouble(2, actualPrice);
            insertProductHistoryStmt.setTimestamp(3, Timestamp.valueOf(now));
            insertProductHistoryStmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> findByUserId(UUID userId) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT_BY_USER_ID_STATEMENT)) {
                stmt.setObject(1, userId);
                ResultSet rs = stmt.executeQuery();

                List<Product> products = new ArrayList<>();
                while (rs.next()) {
                    products.add(map(rs));
                }
                return products;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Product> findByUserId(Connection conn, UUID userId, PageRequest pageRequest) {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT_BY_USER_ID_PAGE_STATEMENT)) {
            stmt.setObject(1, userId);
            stmt.setLong(2, pageRequest.pageSize() + 1);
            stmt.setLong(3, (pageRequest.pageNumber() - 1) * pageRequest.pageSize());
            ResultSet rs = stmt.executeQuery();

            List<Product> products = new ArrayList<>();
            while (rs.next() && products.size() < pageRequest.pageSize()) {
                products.add(map(rs));
            }

            return new Page<>(products, new Page.PageMeta(rs.next()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Product findById(UUID id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT_BY_ID_STATEMENT)) {
                stmt.setObject(1, id);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new EntityNotFoundException(String.format("Product with id=%s not found", id));
                }
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UUID id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement deleteProductStmt =
                             conn.prepareStatement(DELETE_PRODUCT_BY_ID_STATEMENT);
                     PreparedStatement deleteProductHistoryStmt =
                             conn.prepareStatement(DELETE_PRODUCT_HISTORY_BY_PRODUCT_ID_STATEMENT)
                ) {
                    deleteProductHistoryStmt.setObject(1, id);
                    deleteProductHistoryStmt.execute();

                    deleteProductStmt.setObject(1, id);
                    deleteProductStmt.execute();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                log.error("Transaction rolled back due to exception", e);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("user_id")),
                rs.getString("link"),
                rs.getString("name"),
                rs.getDouble("actual_price"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
