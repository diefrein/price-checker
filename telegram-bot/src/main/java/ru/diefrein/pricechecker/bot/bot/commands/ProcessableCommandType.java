package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.exception.IllegalCommandException;
import ru.diefrein.pricechecker.bot.bot.state.UserState;

import java.util.List;

public enum ProcessableCommandType {
    START("start", "Start the bot"),
    REGISTER("register", "Register your profile"),
    SUBSCRIBE("subscribe", "Add a product to track"),
    SUBSCRIPTIONS("subscriptions", "List all tracked products"),
    REMOVE_SUBSCRIPTION("remove_subscription", "List all tracked products");

    private final String command;
    private final String description;

    ProcessableCommandType(String command, String description) {
        this.command = command;
        this.description = description;
    }

    /**
     * @return list of commands that are displayed in menu
     */
    public static List<ProcessableCommandType> getMenuCommands() {
        return List.of(START, REGISTER, SUBSCRIBE, SUBSCRIPTIONS);
    }

    /**
     * Resolves type of incoming command based on input and current user's state
     *
     * @param command incoming command
     * @param state   current user's state
     * @return type of command
     */
    public static ProcessableCommandType getCommandType(Command command, UserState state) {
        if (command.callbackData() == null) {
            if (state == UserState.SUBSCRIBE_WAIT_FOR_LINK) {
                return ProcessableCommandType.SUBSCRIBE;
            }
            return ProcessableCommandType.fromText(command.text());
        } else {
            if (command.callbackData().contains(REMOVE_SUBSCRIPTION.name())) {
                return ProcessableCommandType.REMOVE_SUBSCRIPTION;
            }
            if (command.callbackData().contains(SUBSCRIPTIONS.name())) {
                return ProcessableCommandType.SUBSCRIPTIONS;
            }
            throw new IllegalCommandException("Cannot resolve command type");
        }
    }

    private static ProcessableCommandType fromText(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalCommandException("Text cannot be null or empty");
        }
        for (ProcessableCommandType processableCommandType : ProcessableCommandType.values()) {
            if (text.contains("/%s".formatted(processableCommandType.command))) {
                return processableCommandType;
            }
        }
        throw new IllegalCommandException("Text=%s doesn't contain processable commands".formatted(text));
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }
}
