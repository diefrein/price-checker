package ru.diefrein.pricechecker.bot.bot.commands;

public record Command(long chatId,
                      String text,
                      String username,
                      String callbackData) {
}
