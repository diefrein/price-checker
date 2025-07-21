package ru.diefrein.pricechecker.transport.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.service.UserService;
import ru.diefrein.pricechecker.storage.entity.User;
import ru.diefrein.pricechecker.storage.exception.EntityNotFoundException;
import ru.diefrein.pricechecker.transport.exception.DeserializationException;
import ru.diefrein.pricechecker.transport.request.CreateUserRequest;
import ru.diefrein.pricechecker.transport.request.UpdateUserRequest;
import ru.diefrein.pricechecker.util.ControllerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class UserHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private static final String USER_PREFIX = "/users/";
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserHandler(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case ControllerUtils.POST -> handlePost(exchange);
                case ControllerUtils.PATCH -> handlePatch(exchange);
                case ControllerUtils.GET -> handleGet(exchange);
                default -> exchange.sendResponseHeaders(405, -1);
            }
        } catch (EntityNotFoundException e) {
            sendErrorResponse(e.getMessage(), 404, exchange);
        } catch (Exception e) {
            String errorResponse = "Unexpected error while handling HTTP request";
            sendErrorResponse(errorResponse, 500, exchange);
            log.error("Unexpected error while handling HTTP request", e);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            User user = createUser(is);
            sendOkResponse(user, exchange);
        }
    }

    private void handlePatch(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            updateUser(is);
            exchange.sendResponseHeaders(201, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.endsWith("/users")) {
            List<User> users = userService.findAll();
            sendOkResponse(users, exchange);
        } else {
            String[] pathParts = ControllerUtils.getPathAfterPrefix(path, USER_PREFIX).split("/");

            if (pathParts.length == 1) {
                String userId = pathParts[0];
                User user = userService.findById(UUID.fromString(userId));
                sendOkResponse(user, exchange);
            } else {
                exchange.sendResponseHeaders(400, -1);
            }
        }
    }

    private User createUser(InputStream is) {
        try {
            CreateUserRequest request = objectMapper.readValue(is, CreateUserRequest.class);
            return userService.create(request.name(), request.isActive());
        } catch (IOException e) {
            throw new DeserializationException("Failed to convert InputStream to CreateUserRequest");
        }
    }

    private void updateUser(InputStream is) {
        try {
            UpdateUserRequest request = objectMapper.readValue(is, UpdateUserRequest.class);
            userService.update(request.id(), request.isActive());
        } catch (IOException e) {
            throw new DeserializationException("Failed to convert InputStream to UpdateUserRequest");
        }
    }

    private void sendOkResponse(Object response, HttpExchange exchange) throws IOException {
        String jsonResponse = objectMapper.writeValueAsString(response);
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private void sendErrorResponse(String errorResponse, int errorCode, HttpExchange exchange) throws IOException {
        byte[] responseBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(errorCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
