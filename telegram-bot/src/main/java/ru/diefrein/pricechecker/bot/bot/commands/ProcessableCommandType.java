package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.exception.IllegalCommandException;
import ru.diefrein.pricechecker.bot.bot.state.UserState;

public enum ProcessableCommandType {
    START("start"), REGISTER("register"), SUBSCRIBE("subscribe"), SUBSCRIPTIONS("subscriptions");

    private final String command;

    ProcessableCommandType(String command) {
        this.command = command;
    }

    /**
     * Resolves type of incoming command based on input and current user's state
     *
     * @param command incoming command
     * @param state current user's state
     * @return type of command
     */
    public static ProcessableCommandType getCommandType(Command command, UserState state) {
        try {
            return ProcessableCommandType.fromText(command.text());
        } catch (IllegalCommandException e) {
            if (state == UserState.SUBSCRIBE_WAIT_FOR_LINK) {
                return ProcessableCommandType.SUBSCRIBE;
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
}
