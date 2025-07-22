package ru.diefrein.pricechecker.bot.transport.exception;

public class DeserializationException extends RuntimeException {

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable throwable) {
        super(message);
    }
}
