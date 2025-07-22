package ru.diefrein.pricechecker.transport.http.request;

public record CreateUserRequest(String name, boolean isActive) {
}
