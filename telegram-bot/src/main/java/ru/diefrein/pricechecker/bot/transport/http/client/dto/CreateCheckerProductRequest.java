package ru.diefrein.pricechecker.bot.transport.http.client.dto;

import java.util.UUID;

public record CreateCheckerProductRequest(UUID userId, String link) {
}
