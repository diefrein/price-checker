package ru.diefrein.pricechecker.transport.http.request;

import java.util.UUID;

public record UpdateUserRequest(UUID id, boolean isActive) {
}
