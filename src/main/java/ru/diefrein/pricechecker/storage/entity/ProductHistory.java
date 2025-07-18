package ru.diefrein.pricechecker.storage.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductHistory(
        UUID productId,
        Double price,
        LocalDateTime priceAtDateTime) {
}
