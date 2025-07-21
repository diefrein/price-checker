package ru.diefrein.pricechecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.configuration.webserver.HttpServerConfigurator;
import ru.diefrein.pricechecker.service.ProductParser;
import ru.diefrein.pricechecker.service.ProductService;
import ru.diefrein.pricechecker.service.SiteParser;
import ru.diefrein.pricechecker.service.UserService;
import ru.diefrein.pricechecker.service.dto.enums.ProcessableSite;
import ru.diefrein.pricechecker.service.impl.GoldAppleParser;
import ru.diefrein.pricechecker.service.impl.LamodaParser;
import ru.diefrein.pricechecker.service.impl.ProductParserImpl;
import ru.diefrein.pricechecker.service.impl.ProductServiceImpl;
import ru.diefrein.pricechecker.service.impl.UserServiceImpl;
import ru.diefrein.pricechecker.service.scheduler.ProductPriceScheduler;
import ru.diefrein.pricechecker.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.storage.repository.ProductRepository;
import ru.diefrein.pricechecker.storage.repository.UserRepository;
import ru.diefrein.pricechecker.storage.repository.impl.ProductRepositoryImpl;
import ru.diefrein.pricechecker.storage.repository.impl.UserRepositoryImpl;
import ru.diefrein.pricechecker.transport.handler.ProductHandler;
import ru.diefrein.pricechecker.transport.handler.UserHandler;

import java.io.IOException;
import java.util.Map;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        Map<ProcessableSite, SiteParser> siteParsers = siteParser();
        ProductParser productParser = new ProductParserImpl(siteParsers);

        ConnectionPool connectionPool = new ConnectionPool();
        ProductRepository productRepository = new ProductRepositoryImpl(connectionPool);
        UserRepository userRepository = new UserRepositoryImpl(connectionPool);

        ProductService productService = new ProductServiceImpl(
                connectionPool,
                productRepository,
                productParser,
                userRepository
        );
        UserService userService = new UserServiceImpl(userRepository);


        HttpServerConfigurator httpServerConfigurator = new HttpServerConfigurator(
                httpHandlers(userService, productService)
        );
        httpServerConfigurator.initHttpServer();

        ProductPriceScheduler productPriceScheduler = new ProductPriceScheduler(productService);
        long stop = System.currentTimeMillis();
        long startupTimeMs = stop - start;
        log.info("Application started in {} ms", startupTimeMs);
    }

    private static Map<ProcessableSite, SiteParser> siteParser() {
        return Map.of(
                ProcessableSite.GOLD_APPLE, new GoldAppleParser(),
                ProcessableSite.LAMODA, new LamodaParser()
        );
    }

    private static Map<String, HttpHandler> httpHandlers(UserService userService, ProductService productService) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return Map.of(
                "/users", new UserHandler(userService, objectMapper),
                "/products", new ProductHandler(productService, objectMapper)
        );
    }
}