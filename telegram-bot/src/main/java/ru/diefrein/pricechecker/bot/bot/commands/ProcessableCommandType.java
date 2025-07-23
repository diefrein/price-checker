package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.exception.IllegalCommandException;

public enum ProcessableCommandType {
    START("start"), REGISTER("register"), SUBSCRIBE("subscribe");

    private final String command;

    ProcessableCommandType(String command) {
        this.command = command;
    }

    public static ProcessableCommandType fromText(String text) {
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
