package ru.diefrein.pricechecker.bot.transport.http.client.dto;

public record CreateCheckerUserRequest(String name, boolean isActive) {
}