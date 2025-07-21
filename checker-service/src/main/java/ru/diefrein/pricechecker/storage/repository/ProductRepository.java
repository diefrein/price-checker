package ru.diefrein.pricechecker.storage.repository;

import ru.diefrein.pricechecker.storage.dto.Page;
import ru.diefrein.pricechecker.storage.dto.PageRequest;
import ru.diefrein.pricechecker.storage.entity.Product;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface ProductRepository {

    Product create(UUID userId, String link, String name, Double actualPrice);

    void update(Connection conn, UUID productId, String name, Double actualPrice);

    List<Product> findByUserId(UUID userId);

    Page<Product> findByUserId(Connection conn, UUID userId, PageRequest pageRequest);

    Product findById(UUID id);

    void remove(UUID id);
}
