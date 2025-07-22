package ru.diefrein.pricechecker.storage.repository.impl;

import ru.diefrein.pricechecker.storage.entity.ProductHistory;
import ru.diefrein.pricechecker.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.storage.repository.ProductHistoryRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductHistoryRepositoryImpl implements ProductHistoryRepository {

    private static final String INSERT_PRODUCT_HISTORY_STATEMENT = """
            INSERT INTO checker.products_history (product_id, price, price_at_date_time) VALUES (?, ?, ?)
            """;
    private static final String SELECT_PRODUCT_HISTORY_BY_PRODUCT_ID_STATEMENT = """
            SELECT * FROM checker.products_history WHERE product_id = ?
            """;

    private final DataSource dataSource;

    public ProductHistoryRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(UUID productId, Double price, LocalDateTime priceAtDateTime) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_PRODUCT_HISTORY_STATEMENT)) {
                stmt.setObject(1, productId);
                stmt.setDouble(2, price);
                stmt.setTimestamp(3, Timestamp.valueOf(priceAtDateTime));
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductHistory> findByProductId(UUID productId) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT_HISTORY_BY_PRODUCT_ID_STATEMENT)) {
                stmt.setObject(1, productId);
                ResultSet rs = stmt.executeQuery();

                List<ProductHistory> productHistory = new ArrayList<>();
                while (rs.next()) {
                    productHistory.add(map(rs));
                }
                return productHistory;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ProductHistory map(ResultSet rs) throws SQLException {
        return new ProductHistory(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("product_id")),
                rs.getDouble("price"),
                rs.getTimestamp("price_at_date_time").toLocalDateTime()
        );
    }
}
