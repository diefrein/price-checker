package ru.diefrein.pricechecker.transport.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.configuration.parameters.WebserverParameterProvider;
import ru.diefrein.pricechecker.util.ControllerUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoggingAndOptionsHandlerDecorator implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(LoggingAndOptionsHandlerDecorator.class);

    private final HttpHandler handler;

    public LoggingAndOptionsHandlerDecorator(HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        logRequest(exchange);

        handleOrigin(exchange);

        if (ControllerUtils.OPTIONS.equalsIgnoreCase(exchange.getRequestMethod())) {
            handleOptionsRequest(exchange);
            return;
        }

        handler.handle(exchange);
    }

    private void logRequest(HttpExchange exchange) throws IOException {
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
    }

    private void handleOrigin(HttpExchange exchange) {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (WebserverParameterProvider.FRONTEND_URL.equals(origin)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", origin);
            exchange.getResponseHeaders().add("Vary", "Origin");
        }
    }

    private void handleOptionsRequest(HttpExchange exchange) throws IOException {
        // Handle preflight CORS request
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }
}
