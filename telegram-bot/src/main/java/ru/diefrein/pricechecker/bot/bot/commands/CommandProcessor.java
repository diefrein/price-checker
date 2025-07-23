package ru.diefrein.pricechecker.bot.bot.commands;

import ru.diefrein.pricechecker.bot.bot.state.UserState;

public interface CommandProcessor {

    /**
     * Process user command
     *
     * @param command user's command
     * @param state user's state
     * @return message to be sent to user
     */
    ProcessResult process(Command command, UserState state);

    ProcessableCommandType getProcessableCommandType();
}
