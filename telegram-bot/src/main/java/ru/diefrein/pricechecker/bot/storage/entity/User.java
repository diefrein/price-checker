package ru.diefrein.pricechecker.bot.storage.entity;

import ru.diefrein.pricechecker.bot.bot.state.UserState;

import java.util.UUID;

public record User(long telegramId, UUID checkerUserId, UserState userState) {
}
