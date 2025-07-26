package ru.diefrein.pricechecker.common.client;

import ru.diefrein.pricechecker.common.client.dto.CheckerProduct;
import ru.diefrein.pricechecker.common.client.dto.CreateCheckerProductRequest;
import ru.diefrein.pricechecker.common.client.dto.CreateCheckerUserRequest;

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
     * @param checkerUserId id of user in checker-db
     * @return list of products
     * @deprecated use paginated version
     */
    @Deprecated(forRemoval = true, since = "26.07.2025")
    List<CheckerProduct> getUserProducts(UUID checkerUserId);

    /**
     * Delete user's product by link
     *
     * @param productId id of product in checker-db
     */
    void removeProduct(UUID productId);
}
