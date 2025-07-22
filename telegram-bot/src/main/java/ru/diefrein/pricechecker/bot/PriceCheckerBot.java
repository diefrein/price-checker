package ru.diefrein.pricechecker.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.ProductService;
import ru.diefrein.pricechecker.bot.service.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceCheckerBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(PriceCheckerBot.class);

    private final Map<Long, Boolean> waitingForLink = new ConcurrentHashMap<>();

    private final String botUsername = BotParameterProvider.NAME;

    private final UserService userService;
    private final ProductService productService;

    public PriceCheckerBot(UserService userService, ProductService productService) {
        super(BotParameterProvider.TOKEN);
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    public String getBotToken() {
        return BotParameterProvider.TOKEN;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();

        if (waitingForLink.getOrDefault(chatId, false)) {
            String productLink = text.trim();
            productService.create(chatId, productLink);
            waitingForLink.remove(chatId);
            sendMessage(chatId, "Product link saved successfully!");
            return;
        }

        if (text.equals("/start")) {
            sendWelcomeMessage(chatId);
        } else if (text.equals("/register")) {
            log.info("Received /register command, chatId={}", chatId);
            userService.create(chatId, message.getChat().getUserName());
        } else if (text.contains("/product")) {
            log.info("Received /product command, chatId={}", chatId);
            waitingForLink.put(chatId, true);
        } else {
            sendMessage(chatId, "I don't understand that command. Try /start or /register");
        }
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {
        try {
            super.clearWebhook();
        } catch (TelegramApiRequestException e) {
            log.warn("Webhook not found, skipping clearWebhook");
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        String text = "Welcome to the Price Checker Bot!\n\n" +
                "Use /register to create your account";
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
}