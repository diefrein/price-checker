package ru.diefrein.pricechecker.common.client.dto;

import java.util.UUID;

public record CreateCheckerProductRequest(UUID userId, String link) {
}
