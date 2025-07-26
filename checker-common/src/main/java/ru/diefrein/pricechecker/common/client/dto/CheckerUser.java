package ru.diefrein.pricechecker.common.client.dto;

import java.util.UUID;

public record CheckerUser(UUID id, String name, boolean isActive) {
}
