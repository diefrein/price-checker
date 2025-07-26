package ru.diefrein.pricechecker.common.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.diefrein.pricechecker.common.client.dto.CheckerProduct;
import ru.diefrein.pricechecker.common.client.dto.CheckerUser;
import ru.diefrein.pricechecker.common.client.dto.CreateCheckerProductRequest;
import ru.diefrein.pricechecker.common.client.dto.CreateCheckerUserRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CheckerServiceClientImpl implements CheckerServiceClient {

    private final ObjectMapper objectMapper;
    private final CheckerClientParameters parameters;

    public CheckerServiceClientImpl(ObjectMapper objectMapper,
                                    CheckerClientParameters parameters) {
        this.parameters = parameters;
        this.objectMapper = objectMapper;
    }

    @Override
    public UUID createUser(CreateCheckerUserRequest request) {
        try {
            String response = sendPostRequest("/users", objectMapper.writeValueAsString(request));
            CheckerUser checkerUser = objectMapper.readValue(response, CheckerUser.class);
            return checkerUser.id();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createProduct(CreateCheckerProductRequest request) {
        try {
            sendPostRequest("/products", objectMapper.writeValueAsString(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CheckerProduct> getUserProducts(UUID checkerUserId) {
        try {
            String response = sendGetRequest("/products", Map.of("userId", checkerUserId.toString()));
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProduct(UUID productId) {
        try {
            sendDeleteRequest("/products/%s".formatted(productId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sendGetRequest(String endpoint, Map<String, String> queryParams) throws IOException {
        StringBuilder urlWithParams = new StringBuilder(parameters.baseUrl() + endpoint);

        if (queryParams != null && !queryParams.isEmpty()) {
            urlWithParams.append("?");
            urlWithParams.append(queryParams.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                            + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&")));
        }

        URL url = new URL(urlWithParams.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(parameters.connectionTimeout());

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        } else {
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }

    private String sendPostRequest(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(parameters.baseUrl() + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK
                || responseCode == HttpURLConnection.HTTP_CREATED) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        } else {
            throw new IOException("POST request failed with response code: " + responseCode);
        }
    }

    private void sendDeleteRequest(String endpoint) throws IOException {
        URL url = new URL(parameters.baseUrl() + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setConnectTimeout(parameters.connectionTimeout());

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new IOException("DELETE request failed with response code: " + responseCode);
        }
    }
}
