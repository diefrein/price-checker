package ru.diefrein.pricechecker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpHandler;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.configuration.parameters.KafkaParameterProvider;
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
import ru.diefrein.pricechecker.transport.http.handler.ProductHandler;
import ru.diefrein.pricechecker.transport.http.handler.UserHandler;
import ru.diefrein.pricechecker.transport.kafka.producer.PriceChangeProducer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        ConnectionPool connectionPool = new ConnectionPool();
        UserRepository userRepository = new UserRepositoryImpl(connectionPool.getDataSource());

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        UserService userService = new UserServiceImpl(userRepository);
        ProductService productService = getProductService(connectionPool, userRepository);

        HttpServerConfigurator httpServerConfigurator = new HttpServerConfigurator(
                httpHandlers(userService, productService, objectMapper)
        );
        httpServerConfigurator.initHttpServer();

        ProductPriceScheduler productPriceScheduler = new ProductPriceScheduler(productService);
        long stop = System.currentTimeMillis();
        long startupTimeMs = stop - start;
        log.info("Application started in {} ms", startupTimeMs);
    }

    private static ProductService getProductService(ConnectionPool connectionPool, UserRepository userRepository) {
        Map<ProcessableSite, SiteParser> siteParsers = siteParser();
        ProductParser productParser = new ProductParserImpl(siteParsers);

        ProductRepository productRepository = new ProductRepositoryImpl(connectionPool.getDataSource());

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PriceChangeProducer priceChangeProducer = getPriceChangeProducer(objectMapper);

        return new ProductServiceImpl(
                connectionPool,
                productRepository,
                productParser,
                userRepository,
                priceChangeProducer);
    }

    private static PriceChangeProducer getPriceChangeProducer(ObjectMapper objectMapper) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaParameterProvider.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaParameterProvider.KEY_SERIALIZER_CLASS_CONFIG);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaParameterProvider.VALUE_SERIALIZER_CLASS_CONFIG);
        return new PriceChangeProducer(
                objectMapper,
                KafkaParameterProvider.PRICE_CHANGE_TOPIC,
                props
        );
    }

    private static Map<ProcessableSite, SiteParser> siteParser() {
        return Map.of(
                ProcessableSite.GOLD_APPLE, new GoldAppleParser(),
                ProcessableSite.LAMODA, new LamodaParser()
        );
    }

    private static Map<String, HttpHandler> httpHandlers(UserService userService,
                                                         ProductService productService,
                                                         ObjectMapper objectMapper) {
        return Map.of(
                "/users", new UserHandler(userService, objectMapper),
                "/products", new ProductHandler(productService, objectMapper)
        );
    }
}