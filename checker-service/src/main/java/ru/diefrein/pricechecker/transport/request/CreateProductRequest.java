package ru.diefrein.pricechecker.transport.request;

import java.util.UUID;

public record CreateProductRequest(UUID userId, String link) {
}
