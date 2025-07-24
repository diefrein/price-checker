package ru.diefrein.pricechecker.bot.transport.http.client;

import ru.diefrein.pricechecker.bot.transport.http.client.dto.CheckerProduct;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CreateCheckerProductRequest;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CreateCheckerUserRequest;

import java.util.List;
import java.util.UUID;

/**
 * Web client to call checker-service endpoints
 */
public interface CheckerServiceClient {

    /**
     * Create user in checker-db
     *
     * @param request creation request
     * @return id of created user
     */
    UUID createUser(CreateCheckerUserRequest request);

    /**
     * Create product that will be followed
     *
     * @param request product creation request
     */
    void createProduct(CreateCheckerProductRequest request);

    /**
     * Get products that are followed by user
     *
     * @param checkerUserId id of user in checher-db
     * @return list of products
     */
    List<CheckerProduct> getUserProducts(UUID checkerUserId);
}
