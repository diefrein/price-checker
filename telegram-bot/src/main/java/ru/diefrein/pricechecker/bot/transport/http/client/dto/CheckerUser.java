package ru.diefrein.pricechecker.bot.transport.http.client.dto;

import java.util.UUID;

public record CheckerUser(UUID id, String name, boolean isActive) {
}
