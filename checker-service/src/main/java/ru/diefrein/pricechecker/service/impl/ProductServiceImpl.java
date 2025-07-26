package ru.diefrein.pricechecker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;
import ru.diefrein.pricechecker.configuration.parameters.ProductServiceParameterProvider;
import ru.diefrein.pricechecker.service.ProductParser;
import ru.diefrein.pricechecker.service.ProductService;
import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.storage.entity.Product;
import ru.diefrein.pricechecker.storage.entity.User;
import ru.diefrein.pricechecker.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.storage.repository.ProductRepository;
import ru.diefrein.pricechecker.storage.repository.UserRepository;
import ru.diefrein.pricechecker.transport.kafka.dto.PriceChangeEvent;
import ru.diefrein.pricechecker.transport.kafka.producer.PriceChangeProducer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductParser parser;
    private final DataSource dataSource;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PriceChangeProducer priceChangeProducer;

    public ProductServiceImpl(ConnectionPool connectionPool,
                              ProductRepository productRepository,
                              ProductParser parser,
                              UserRepository userRepository, PriceChangeProducer priceChangeProducer) {
        this.dataSource = connectionPool.getDataSource();
        this.productRepository = productRepository;
        this.parser = parser;
        this.userRepository = userRepository;
        this.priceChangeProducer = priceChangeProducer;
    }

    @Override
    public void create(UUID userId, String link) {
        ParsedProduct parsedProduct;
        try {
            parsedProduct = parser.getProduct(link);
        } catch (Exception e) {
            log.error("Failed to parse product, unable to persist it to database", e);
            throw e;
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
    public Page<Product> findByUserId(UUID userId, PageRequest pageRequest) {
        return productRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public void remove(UUID id) {
        productRepository.remove(id);
    }

    private void checkForUpdatesInTransaction(Connection conn) {
        int pageNumber = 1;
        Page<User> activeUsers;
        do {
            activeUsers = userRepository.findActiveUsers(
                    conn,
                    new PageRequest(ProductServiceParameterProvider.USERS_PAGE_SIZE, pageNumber++)
            );
            for (User activeUser : activeUsers.data()) {
                log.info("Finding products for user with id={}", activeUser.id());
                processUserProducts(conn, activeUser.id());
            }
        } while (activeUsers.meta().hasNext());

    }

    private void processUserProducts(Connection conn, UUID userId) {
        int pageNumber = 1;
        int processedCount = 0;
        Page<Product> products;
        do {
            products = productRepository.findByUserId(
                    conn,
                    userId,
                    new PageRequest(ProductServiceParameterProvider.PRODUCTS_PAGE_SIZE, pageNumber++)
            );
            for (Product product : products.data()) {
                ParsedProduct parsedProduct = parser.getProduct(product.link());
                if (isProductInfoChanged(parsedProduct, product)) {
                    productRepository.update(
                            conn,
                            product.id(),
                            parsedProduct.name(),
                            parsedProduct.actualPrice()
                    );
                    priceChangeProducer.send(PriceChangeEvent.fromProductUpdate(product, parsedProduct));
                }
            }
            processedCount += products.data().size();
        } while (products.meta().hasNext());

        if (processedCount > 0) {
            log.info("Processed {} products for user with id={}", processedCount, userId);
        }
    }

    private boolean isProductInfoChanged(ParsedProduct newProductData, Product oldProductData) {
        return !(newProductData.name().equals(oldProductData.name())
                && newProductData.actualPrice().equals(oldProductData.actualPrice()));
    }

}
