package ru.diefrein.pricechecker.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.exception.CommandProcessorNotFoundException;
import ru.diefrein.pricechecker.bot.bot.exception.IllegalCommandException;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException;

import java.util.Map;
import java.util.Set;

public class PriceCheckerBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(PriceCheckerBot.class);
    private static final String UNKNOWN_COMMAND_RESPONSE = "Unknown command. Type /start to list all available commands";
    private static final String USER_NOT_FOUND_RESPONSE = "User not found. Please use /register first";
    private static final String GENERAL_ERROR_RESPONSE = "Unknown error, please contact system administrator";
    private static final String botUsername = BotParameterProvider.NAME;
    private static final Set<ProcessableCommandType> COMMANDS_WITHOUT_PERSISTED_USER =
            Set.of(ProcessableCommandType.START, ProcessableCommandType.REGISTER);

    private final Map<ProcessableCommandType, CommandProcessor> processors;
    private final UserService userService;

    public PriceCheckerBot(Map<ProcessableCommandType, CommandProcessor> processors, UserService userService) {
        super(BotParameterProvider.TOKEN);
        this.processors = processors;
        this.userService = userService;
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
        Command command = map(update);
        long chatId = command.chatId();

        try {
            ProcessableCommandType commandType = ProcessableCommandType.fromText(command.text());

            CommandProcessor processor = getCommandProcessor(commandType);
            UserState state = getUserState(commandType, chatId);

            log.info("Processing command={}, chatId={}, userState={}", commandType, chatId, state);
            ProcessResult processResult = processor.process(command, state);
            sendMessage(chatId, processResult.response());
        } catch (IllegalCommandException e) {
            log.error("Illegal command, chatId={}", chatId, e);
            sendMessage(chatId, UNKNOWN_COMMAND_RESPONSE);
        } catch (CommandProcessorNotFoundException e) {
            log.error("No command processor found, chatId={}", chatId, e);
            sendMessage(chatId, UNKNOWN_COMMAND_RESPONSE);
        } catch (EntityNotFoundException e) {
            log.error("User not found for chatId={}", chatId, e);
            sendMessage(chatId, USER_NOT_FOUND_RESPONSE);
        } catch (Exception e) {
            log.error("Exception while handling command, chatId={}", chatId, e);
            sendMessage(chatId, GENERAL_ERROR_RESPONSE);
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

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message", e);
        }
    }

    private Command map(Update update) {
        Message message = update.getMessage();
        return new Command(
                message.getChatId(),
                message.getText(),
                message.getChat().getUserName()
        );
    }

    private CommandProcessor getCommandProcessor(ProcessableCommandType commandType) {
        if (!processors.containsKey(commandType)) {
            throw new CommandProcessorNotFoundException(
                    "No command processor found for command=%s".formatted(commandType));
        }

        return processors.get(commandType);
    }

    private UserState getUserState(ProcessableCommandType commandType, long chatId) {
        if (COMMANDS_WITHOUT_PERSISTED_USER.contains(commandType)) {
            return UserState.INITIAL;
        }
        User user = userService.findByTelegramId(chatId);
        return user.userState();
    }
}