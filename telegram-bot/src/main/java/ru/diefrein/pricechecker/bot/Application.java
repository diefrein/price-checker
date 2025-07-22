package ru.diefrein.pricechecker.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.diefrein.pricechecker.bot.service.ProductService;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.service.impl.ProductServiceImpl;
import ru.diefrein.pricechecker.bot.service.impl.UserServiceImpl;
import ru.diefrein.pricechecker.bot.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.bot.storage.repository.impl.UserRepositoryImpl;
import ru.diefrein.pricechecker.bot.transport.client.CheckerServiceClient;
import ru.diefrein.pricechecker.bot.transport.client.CheckerServiceClientImpl;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        CheckerServiceClient checkerServiceClient = new CheckerServiceClientImpl();

        ConnectionPool connectionPool = new ConnectionPool();
        UserRepository userRepository = new UserRepositoryImpl(connectionPool);
        UserService userService = new UserServiceImpl(userRepository, checkerServiceClient);

        ProductService productService = new ProductServiceImpl(checkerServiceClient, userRepository);

        PriceCheckerBot bot = new PriceCheckerBot(userService, productService);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            log.info("Bot started");
        } catch (TelegramApiRequestException e) {
            log.warn("Exception while starting bot", e);
        } catch (Exception e) {
            log.error("Exception while starting bot", e);
        }
    }
}
