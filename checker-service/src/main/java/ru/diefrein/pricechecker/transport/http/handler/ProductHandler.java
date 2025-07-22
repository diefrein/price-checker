package ru.diefrein.pricechecker.transport.http.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.service.ProductService;
import ru.diefrein.pricechecker.storage.entity.Product;
import ru.diefrein.pricechecker.storage.exception.EntityNotFoundException;
import ru.diefrein.pricechecker.transport.http.exception.DeserializationException;
import ru.diefrein.pricechecker.transport.http.request.CreateProductRequest;
import ru.diefrein.pricechecker.util.ControllerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class ProductHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(ProductHandler.class);

    private static final String PRODUCT_PREFIX = "/products/";
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ProductHandler(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case ControllerUtils.POST -> handlePost(exchange);
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
            createProduct(is);
            exchange.sendResponseHeaders(201, -1);
        }
    }

    private void createProduct(InputStream is) {
        try {
            CreateProductRequest request = objectMapper.readValue(is, CreateProductRequest.class);
            productService.create(request.userId(), request.link());
        } catch (IOException e) {
            throw new DeserializationException("Failed to convert InputStream to CreateProductRequest");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.endsWith("/products")) {
            exchange.sendResponseHeaders(400, -1);
        } else {
            String[] pathParts = ControllerUtils.getPathAfterPrefix(path, PRODUCT_PREFIX).split("/");

            if (pathParts.length == 1) {
                String userId = pathParts[0];
                List<Product> products = productService.findByUserId(UUID.fromString(userId));
                sendOkResponse(products, exchange);
            } else {
                exchange.sendResponseHeaders(400, -1);
            }
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
