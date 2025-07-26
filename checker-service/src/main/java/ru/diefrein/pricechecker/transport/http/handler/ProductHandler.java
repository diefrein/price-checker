package ru.diefrein.pricechecker.transport.http.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;
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
import java.util.Map;
import java.util.UUID;

public class ProductHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(ProductHandler.class);

    private static final String PRODUCT_PREFIX = "/products/";
    private static final String USER_ID_QUERY_PARAM_KEY = "userId";
    private static final String PAGE_NUMBER_PARAM_KEY = "pageNumber";
    private static final String PAGE_SIZE_PARAM_KEY = "pageSize";
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
                case ControllerUtils.DELETE -> handleDelete(exchange);
                default -> exchange.sendResponseHeaders(405, -1);
            }
        } catch (IllegalArgumentException e) {
            sendErrorResponse(e.getMessage(), 400, exchange);
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
        String query = exchange.getRequestURI().getQuery();

        if (path.endsWith("/products") && query.isEmpty()) {
            exchange.sendResponseHeaders(400, -1);
        } else {
            Map<String, String> queryParams = ControllerUtils.parseQueryParams(query);
            String userId = queryParams.get(USER_ID_QUERY_PARAM_KEY);
            String pageNumber = queryParams.get(PAGE_NUMBER_PARAM_KEY);
            String pageSize = queryParams.get(PAGE_SIZE_PARAM_KEY);
            if (userId != null && pageNumber != null && pageSize != null) {
                PageRequest pageRequest = new PageRequest(
                        Long.parseLong(pageSize),
                        Long.parseLong(pageNumber)
                );
                Page<Product> products = productService.findByUserId(UUID.fromString(userId), pageRequest);
                sendOkResponse(products, exchange);
            } else {
                exchange.sendResponseHeaders(400, -1);
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.endsWith("/products")) {
            exchange.sendResponseHeaders(400, -1);
        } else {
            String[] pathParts = ControllerUtils.getPathAfterPrefix(path, PRODUCT_PREFIX).split("/");
            if (pathParts.length == 1) {
                String productId = pathParts[0];
                productService.remove(UUID.fromString(productId));
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(204, -1);
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
