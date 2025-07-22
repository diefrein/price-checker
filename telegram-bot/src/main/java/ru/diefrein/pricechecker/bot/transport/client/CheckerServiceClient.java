package ru.diefrein.pricechecker.bot.transport.client;

import ru.diefrein.pricechecker.bot.transport.client.dto.CreateCheckerProductRequest;
import ru.diefrein.pricechecker.bot.transport.client.dto.CreateCheckerUserRequest;

import java.util.UUID;

public interface CheckerServiceClient {

    UUID createUser(CreateCheckerUserRequest request);

    void createProduct(CreateCheckerProductRequest request);
}
