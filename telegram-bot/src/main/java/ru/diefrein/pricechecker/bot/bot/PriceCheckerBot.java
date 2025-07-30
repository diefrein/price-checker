package ru.diefrein.pricechecker.bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.exception.CommandProcessorNotFoundException;
import ru.diefrein.pricechecker.bot.bot.exception.IllegalCommandException;
import ru.diefrein.pricechecker.bot.bot.response.ResponseCreator;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.exception.DuplicateEntityException;
import ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException;

import java.util.List;
import java.util.Map;

public class PriceCheckerBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(PriceCheckerBot.class);

    private final Map<ProcessableCommandType, CommandProcessor> processors;
    private final UserService userService;
    private final ResponseCreator responseCreator;

    public PriceCheckerBot(Map<ProcessableCommandType, CommandProcessor> processors,
                           UserService userService,
                           ResponseCreator responseCreator) {
        super(BotParameterProvider.TOKEN);
        this.processors = processors;
        this.userService = userService;
        this.responseCreator = responseCreator;
        registerBotMenu(processors);
    }

    @Override
    public String getBotUsername() {
        return BotParameterProvider.NAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("update={}", update);
        Command command = map(update);
        long chatId = command.chatId();

        try {
            UserState state = getUserState(chatId);
            ProcessableCommandType commandType = ProcessableCommandType.getCommandType(command, state);

            CommandProcessor processor = getCommandProcessor(commandType);

            log.info("Processing command={}, chatId={}, userState={}", commandType, chatId, state);
            ProcessResult processResult = command.callbackData() == null
                    ? processor.process(command, state)
                    : processor.processCallback(command, state);

            if (processResult.newState() != state) {
                userService.updateStateByTelegramId(chatId, processResult.newState());
            }
            sendMessage(processResult, command);
        } catch (IllegalCommandException e) {
            log.error("Illegal command, chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.UNKNOWN_COMMAND_RESPONSE);
        } catch (CommandProcessorNotFoundException e) {
            log.error("No command processor found, chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.UNKNOWN_COMMAND_RESPONSE);
        } catch (EntityNotFoundException e) {
            log.error("User not found for chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.USER_NOT_FOUND_RESPONSE);
        } catch (DuplicateEntityException e) {
            log.error("Duplicate entity found for chatId={}", chatId, e);
            sendMessage(chatId, BotParameterProvider.USER_ALREADY_EXISTS_RESPONSE);
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

    /**
     * Send message into the chat with user
     *
     * @param chatId id of chat
     * @param text   message
     */
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

    /**
     * Send message into the chat with user
     *
     * @param processResult result of command handling (may contain text, buttons, etc)
     * @param command       user command
     */
    public void sendMessage(ProcessResult processResult, Command command) {
        try {
            execute(responseCreator.createResponse(processResult, command));
        } catch (TelegramApiException e) {
            log.error("Failed to send message", e);
        }
    }

    private void registerBotMenu(Map<ProcessableCommandType, CommandProcessor> processors) {
        List<BotCommand> commandList = processors.keySet().stream()
                .filter(commandType -> ProcessableCommandType.getMenuCommands().contains(commandType))
                .map(this::getFromProcessableCommandType)
                .toList();

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commandList);
        setMyCommands.setScope(new BotCommandScopeDefault());

        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            log.error("Failed to initialize menu", e);
        }
    }

    private BotCommand getFromProcessableCommandType(ProcessableCommandType commandType) {
        return new BotCommand("/%s".formatted(commandType.getCommand()), commandType.getDescription());
    }

    private Command map(Update update) {
        if (update.getMessage() != null) {
            Message message = update.getMessage();
            return new Command(
                    message.getChatId(),
                    message.getText(),
                    message.getChat().getUserName(),
                    message.getMessageId(),
                    null
            );
        } else {
            MaybeInaccessibleMessage message = update.getCallbackQuery().getMessage();
            return new Command(
                    message.getChatId(),
                    null,
                    null,
                    message.getMessageId(),
                    update.getCallbackQuery().getData()
            );
        }
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
