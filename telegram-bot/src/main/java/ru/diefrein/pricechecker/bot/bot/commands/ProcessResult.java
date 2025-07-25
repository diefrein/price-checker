package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.buttons.ButtonLayout;
import ru.diefrein.pricechecker.bot.bot.state.UserState;

public record ProcessResult(String response, UserState newState, ButtonLayout buttonLayout) {

    /**
     * Get result with initial state param
     *
     * @param response message to user
     * @return result
     */
    public static ProcessResult toInitialState(String response) {
        return new ProcessResult(response, UserState.INITIAL, null);
    }

    /**
     * Get result with initial state param
     *
     * @param response     message to user
     * @param buttonLayout buttons to be displayed
     * @return result
     */
    public static ProcessResult toInitialState(String response, ButtonLayout buttonLayout) {
        return new ProcessResult(response, UserState.INITIAL, buttonLayout);
    }

}
