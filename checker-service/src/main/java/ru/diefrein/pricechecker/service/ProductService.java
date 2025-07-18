package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.storage.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    void create(UUID userId, String link);

    void checkForUpdates();

    List<Product> findByUserId(UUID userId);
}
