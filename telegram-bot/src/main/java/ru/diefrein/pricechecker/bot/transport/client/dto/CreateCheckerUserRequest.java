package ru.diefrein.pricechecker.bot.transport.client.dto;

public record CreateCheckerUserRequest(String name, boolean isActive) {
}