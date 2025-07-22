package ru.diefrein.pricechecker.bot.transport.client.dto;

import java.util.UUID;

public record CreateCheckerProductRequest(UUID userId, String link) {
}