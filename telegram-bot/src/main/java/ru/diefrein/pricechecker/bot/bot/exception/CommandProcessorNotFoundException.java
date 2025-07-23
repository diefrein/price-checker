package ru.diefrein.pricechecker.bot.bot.exception;

public class CommandProcessorNotFoundException extends RuntimeException {
    public CommandProcessorNotFoundException(String message) {
        super(message);
    }
}
