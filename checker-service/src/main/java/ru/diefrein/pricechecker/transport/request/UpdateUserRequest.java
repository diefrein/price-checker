package ru.diefrein.pricechecker.transport.request;

import java.util.UUID;

public record UpdateUserRequest(UUID id, boolean isActive) {
}
