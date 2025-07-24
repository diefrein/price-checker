package ru.diefrein.pricechecker.bot.transport.http.client.dto;

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
