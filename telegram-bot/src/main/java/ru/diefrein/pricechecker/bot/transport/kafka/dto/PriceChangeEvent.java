package ru.diefrein.pricechecker.bot.transport.kafka.dto;

import java.util.UUID;

public record PriceChangeEvent(UUID userId, String productName, String link, Double newPrice, Double oldPrice) {
}
