package ru.diefrein.pricechecker.transport.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoggingHandlerDecorator implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(LoggingHandlerDecorator.class);

    private final HttpHandler handler;

    public LoggingHandlerDecorator(HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.info("Received request: method={}, uri={}, headers={}",
                exchange.getRequestMethod(), exchange.getRequestURI(), exchange.getRequestHeaders());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exchange.getRequestBody().transferTo(baos);
        byte[] bodyBytes = baos.toByteArray();
        String body = new String(bodyBytes, StandardCharsets.UTF_8);
        if (!body.isEmpty()) {
            log.info("Body: " + body);
        }

        // Replace the input stream so the handler can read it again
        InputStream replacementStream = new ByteArrayInputStream(bodyBytes);
        exchange.setStreams(replacementStream, exchange.getResponseBody());

        handler.handle(exchange);
    }
}
