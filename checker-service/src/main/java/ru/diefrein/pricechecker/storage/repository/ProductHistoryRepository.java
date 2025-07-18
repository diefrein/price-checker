package ru.diefrein.pricechecker.storage.repository;

import ru.diefrein.pricechecker.storage.entity.ProductHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProductHistoryRepository {

    void create(UUID productId, Double price, LocalDateTime priceAtDateTime);

    List<ProductHistory> findByProductId(UUID productId);
}
