package ru.diefrein.pricechecker.transport.http.request;

import java.util.UUID;

public record CreateProductRequest(UUID userId, String link) {
}
