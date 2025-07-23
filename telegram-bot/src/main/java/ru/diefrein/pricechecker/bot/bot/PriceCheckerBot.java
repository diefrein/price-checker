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

public class PriceCheckerBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(PriceCheckerBot.class);

    private final Map<ProcessableCommandType, CommandProcessor> processors;
    private final UserService userService;

    public PriceCheckerBot(Map<ProcessableCommandType, CommandProcessor> processors, UserService userService) {
        super(BotParameterProvider.TOKEN);
        this.processors = processors;
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return BotParameterProvider.NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Command command = map(update);
        long chatId = command.chatId();

        try {
            UserState state = getUserState(chatId);
            ProcessableCommandType commandType = ProcessableCommandType.getCommandType(command, state);

            CommandProcessor processor = getCommandProcessor(commandType);

            log.info("Processing command={}, chatId={}, userState={}", commandType, chatId, state);
            ProcessResult processResult = processor.process(command, state);

            if (processResult.newState() != state) {
                userService.updateStateByTelegramId(chatId, processResult.newState());
            }
            sendMessage(chatId, processResult.response());
        } catch (IllegalCommandException e) {
            log.error("Illegal command, chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.UNKNOWN_COMMAND_RESPONSE);
        } catch (CommandProcessorNotFoundException e) {
            log.error("No command processor found, chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.UNKNOWN_COMMAND_RESPONSE);
        } catch (EntityNotFoundException e) {
            log.error("User not found for chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.USER_NOT_FOUND_RESPONSE);
        } catch (Exception e) {
            log.error("Exception while handling command, chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.GENERAL_ERROR_RESPONSE);
        }
    }

    @Override
    public void clearWebhook() {
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

    private UserState getUserState(long chatId) {
        try {
            User user = userService.findByTelegramId(chatId);
            return user.userState();
        } catch (EntityNotFoundException e) {
            return UserState.INITIAL;
        }
    }
}