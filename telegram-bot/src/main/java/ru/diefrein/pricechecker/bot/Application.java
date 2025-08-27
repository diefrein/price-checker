package ru.diefrein.pricechecker.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.diefrein.pricechecker.bot.bot.PriceCheckerBot;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.commands.impl.RegisterProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.impl.RemoveSubscriptionProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.impl.StartProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.impl.SubscribeProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.impl.SubscriptionsProcessor;
import ru.diefrein.pricechecker.bot.bot.response.ResponseCreator;
import ru.diefrein.pricechecker.bot.configuration.parameters.CheckerClientParameterProvider;
import ru.diefrein.pricechecker.bot.configuration.parameters.KafkaParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.service.impl.SubscriptionServiceImpl;
import ru.diefrein.pricechecker.bot.service.impl.UserServiceImpl;
import ru.diefrein.pricechecker.bot.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.bot.storage.repository.impl.UserRepositoryImpl;
import ru.diefrein.pricechecker.bot.transport.kafka.consumer.KafkaConsumerExecutor;
import ru.diefrein.pricechecker.bot.transport.kafka.consumer.PriceChangeProcessor;
import ru.diefrein.pricechecker.common.client.CheckerClientParameters;
import ru.diefrein.pricechecker.common.client.CheckerServiceClient;
import ru.diefrein.pricechecker.common.client.CheckerServiceClientImpl;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CheckerServiceClient checkerServiceClient = new CheckerServiceClientImpl(
                objectMapper,
                new CheckerClientParameters(
                        CheckerClientParameterProvider.CHECKER_SERVICE_URL,
                        CheckerClientParameterProvider.REQUEST_TIMEOUT_MS
                )
        );

        ConnectionPool connectionPool = new ConnectionPool();
        UserRepository userRepository = new UserRepositoryImpl(connectionPool.getDataSource());
        UserService userService = new UserServiceImpl(userRepository, checkerServiceClient);

        SubscriptionService subscriptionService = new SubscriptionServiceImpl(checkerServiceClient, userRepository);

        Map<ProcessableCommandType, CommandProcessor> processors = getProcessors(
                subscriptionService,
                userService,
                objectMapper
        );
        PriceCheckerBot bot = new PriceCheckerBot(processors, userService, new ResponseCreator());

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

    private static Map<ProcessableCommandType, CommandProcessor> getProcessors(SubscriptionService subscriptionService,
                                                                               UserService userService,
                                                                               ObjectMapper objectMapper) {
        Map<ProcessableCommandType, CommandProcessor> processors = new ConcurrentHashMap<>();
        processors.put(ProcessableCommandType.START,
                new StartProcessor());
        processors.put(ProcessableCommandType.REGISTER,
                new RegisterProcessor(userService));
        processors.put(ProcessableCommandType.SUBSCRIBE,
                new SubscribeProcessor(subscriptionService));
        processors.put(ProcessableCommandType.SUBSCRIPTIONS,
                new SubscriptionsProcessor(subscriptionService, objectMapper));
        processors.put(ProcessableCommandType.REMOVE_SUBSCRIPTION,
                new RemoveSubscriptionProcessor(subscriptionService));
        return processors;
    }

    private static KafkaConsumerExecutor getKafkaConsumerExecutor(PriceCheckerBot bot, UserService userService) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KafkaParameterProvider.BOOTSTRAP_SERVERS_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                KafkaParameterProvider.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                KafkaParameterProvider.KEY_DESERIALIZER_CLASS_CONFIG);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                KafkaParameterProvider.VALUE_DESERIALIZER_CLASS_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                KafkaParameterProvider.AUTO_OFFSET_RESET_CONFIG);

        PriceChangeProcessor priceChangeProcessor = new PriceChangeProcessor(bot, userService);
        return new KafkaConsumerExecutor(
                KafkaParameterProvider.PRICE_CHANGE_TOPIC,
                props,
                priceChangeProcessor,
                new ObjectMapper().registerModule(new JavaTimeModule()));
    }
}
