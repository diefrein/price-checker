package ru.diefrein.pricechecker.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.diefrein.pricechecker.bot.configuration.parameters.KafkaParameterProvider;
import ru.diefrein.pricechecker.bot.service.ProductService;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.service.impl.ProductServiceImpl;
import ru.diefrein.pricechecker.bot.service.impl.UserServiceImpl;
import ru.diefrein.pricechecker.bot.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.bot.storage.repository.impl.UserRepositoryImpl;
import ru.diefrein.pricechecker.bot.transport.http.client.CheckerServiceClient;
import ru.diefrein.pricechecker.bot.transport.http.client.CheckerServiceClientImpl;
import ru.diefrein.pricechecker.bot.transport.kafka.consumer.KafkaConsumerExecutor;
import ru.diefrein.pricechecker.bot.transport.kafka.consumer.PriceChangeProcessor;

import java.util.Properties;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        CheckerServiceClient checkerServiceClient = new CheckerServiceClientImpl();

        ConnectionPool connectionPool = new ConnectionPool();
        UserRepository userRepository = new UserRepositoryImpl(connectionPool.getDataSource());
        UserService userService = new UserServiceImpl(userRepository, checkerServiceClient);

        ProductService productService = new ProductServiceImpl(checkerServiceClient, userRepository);

        PriceCheckerBot bot = new PriceCheckerBot(userService, productService);

        KafkaConsumerExecutor kafkaConsumerExecutor = getKafkaConsumerExecutor(bot, userService);
        kafkaConsumerExecutor.start();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("Bot started");
        } catch (TelegramApiRequestException e) {
            log.warn("Exception while starting bot", e);
        } catch (Exception e) {
            log.error("Exception while starting bot", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down application");
            kafkaConsumerExecutor.stop();
        }));
    }

    private static KafkaConsumerExecutor getKafkaConsumerExecutor(PriceCheckerBot bot, UserService userService) {
        PriceChangeProcessor priceChangeProcessor = new PriceChangeProcessor(bot, userService);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaParameterProvider.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaParameterProvider.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaParameterProvider.KEY_DESERIALIZER_CLASS_CONFIG);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaParameterProvider.VALUE_DESERIALIZER_CLASS_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaParameterProvider.AUTO_OFFSET_RESET_CONFIG);

        return new KafkaConsumerExecutor(
                KafkaParameterProvider.PRICE_CHANGE_TOPIC,
                props,
                priceChangeProcessor,
                new ObjectMapper().registerModule(new JavaTimeModule()));
    }
}
