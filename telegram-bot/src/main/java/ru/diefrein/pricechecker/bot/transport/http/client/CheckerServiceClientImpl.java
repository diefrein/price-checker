package ru.diefrein.pricechecker.bot.transport.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.bot.configuration.parameters.CheckerClientParameterProvider;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CheckerUser;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CreateCheckerProductRequest;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CreateCheckerUserRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CheckerServiceClientImpl implements CheckerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CheckerServiceClientImpl.class);
    private final String baseUrl = String.format(
            "%s:%s",
            CheckerClientParameterProvider.CHECKER_SERVICE_HOST,
            CheckerClientParameterProvider.CHECKER_SERVICE_PORT
    );
    private final ObjectMapper objectMapper = new ObjectMapper();
    public CheckerServiceClientImpl() {
        log.info("baseurl={}", baseUrl);
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

    private String sendGetRequest(String endpoint) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
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
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }

    private String sendPostRequest(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK ||
                responseCode == HttpURLConnection.HTTP_CREATED) {
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

}
