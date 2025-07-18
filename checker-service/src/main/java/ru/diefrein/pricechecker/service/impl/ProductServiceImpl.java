package ru.diefrein.pricechecker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.service.ProductParser;
import ru.diefrein.pricechecker.service.ProductService;
import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.storage.entity.Product;
import ru.diefrein.pricechecker.storage.entity.User;
import ru.diefrein.pricechecker.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.storage.repository.ProductRepository;
import ru.diefrein.pricechecker.storage.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductParser parser;
    private final DataSource dataSource;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ConnectionPool connectionPool,
                              ProductRepository productRepository,
                              ProductParser parser,
                              UserRepository userRepository) {
        this.dataSource = connectionPool.getDataSource();
        this.productRepository = productRepository;
        this.parser = parser;
        this.userRepository = userRepository;
    }

    @Override
    public void create(UUID userId, String link) {
        ParsedProduct parsedProduct;
        try {
            parsedProduct = parser.getProduct(link);
        } catch (Exception e) {
            log.error("Failed to parse product, unable to persist it to database", e);
            return;
        }

        productRepository.create(userId, link, parsedProduct.name(), parsedProduct.actualPrice());
    }

    @Override
    public void checkForUpdates() {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                checkForUpdatesInTransaction(conn);
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

    @Override
    public List<Product> findByUserId(UUID userId) {
        return productRepository.findByUserId(userId);
    }

    private void checkForUpdatesInTransaction(Connection conn) {
        List<User> activeUsers = userRepository.findActiveUsers(conn);
        for (User activeUser : activeUsers) {
            processUserProducts(conn, activeUser.id());
        }
    }

    private void processUserProducts(Connection conn, UUID userId) {
        List<Product> products = productRepository.findByUserId(conn, userId);
        int processedCount = 0;
        for (Product product : products) {
            ParsedProduct parsedProduct = parser.getProduct(product.link());
            if (isProductInfoChanged(parsedProduct, product)) {
                productRepository.update(
                        conn,
                        product.id(),
                        parsedProduct.name(),
                        parsedProduct.actualPrice()
                );
                processedCount++;
            }
        }
        if (processedCount > 0) {
            log.info("Processed {} products for user with id={}", processedCount, userId);
        }
    }

    private boolean isProductInfoChanged(ParsedProduct newProductData, Product oldProductData) {
        return !(newProductData.name().equals(oldProductData.name())
                && newProductData.actualPrice().equals(oldProductData.actualPrice()));
    }

}
