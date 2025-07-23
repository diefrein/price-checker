package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.state.UserState;

public record ProcessResult(String response, UserState newState) {

    public static ProcessResult toInitialState(String response) {
        return new ProcessResult(response, UserState.INITIAL);
    }
}
