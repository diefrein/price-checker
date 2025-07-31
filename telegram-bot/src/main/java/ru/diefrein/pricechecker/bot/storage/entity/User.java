package ru.diefrein.pricechecker.bot.storage.entity;

import ru.diefrein.pricechecker.bot.bot.state.UserState;

import java.util.UUID;

public record User(long chatId, UUID checkerUserId, UserState userState) {
}
