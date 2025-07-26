package ru.diefrein.pricechecker.common.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheckerProduct(UUID id,
                             UUID userId,
                             String link,
                             String name,
                             Double actualPrice,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
}
