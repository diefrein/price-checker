package ru.diefrein.pricechecker.transport.http.exception;

public class DeserializationException extends RuntimeException {

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable throwable) {
        super(message);
    }
}
