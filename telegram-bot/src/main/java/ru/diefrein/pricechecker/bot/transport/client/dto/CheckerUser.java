package ru.diefrein.pricechecker.bot.transport.client.dto;

import java.util.UUID;

public record CheckerUser(UUID id, String name, boolean isActive) {
}
