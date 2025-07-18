package ru.diefrein.pricechecker.storage.entity;

import java.util.UUID;

public record User(UUID id, String name, boolean isActive) {
}
