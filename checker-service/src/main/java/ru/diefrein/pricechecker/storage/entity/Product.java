package ru.diefrein.pricechecker.storage.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record Product(
        UUID id,
        UUID userId,
        String link,
        String name,
        Double actualPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
