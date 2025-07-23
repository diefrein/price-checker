package ru.diefrein.pricechecker.bot.bot.exception;

public class IllegalCommandException extends RuntimeException {
    public IllegalCommandException(String message) {
        super(message);
    }
}
