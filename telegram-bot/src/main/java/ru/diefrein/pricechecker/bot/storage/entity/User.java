package ru.diefrein.pricechecker.bot.storage.entity;

import java.util.UUID;

public record User(long telegramId, UUID checkerUserId) {
}
