package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.state.UserState;

/**
 * Class that processes incoming user's command
 */
public interface CommandProcessor {

    /**
     * Process user command
     *
     * @param command user's command
     * @param state   user's state
     * @return message to be sent to user
     */
    ProcessResult process(Command command, UserState state);

    /**
     * Process callback to user's command
     *
     * @param command command with callback
     * @param state   user's state
     * @return message to be sent to user
     */
    default ProcessResult processCallback(Command command, UserState state) {
        throw new UnsupportedOperationException("Method is not implemented for callback commands");
    }
}
