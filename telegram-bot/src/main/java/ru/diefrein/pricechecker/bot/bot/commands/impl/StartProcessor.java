package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;

public class StartProcessor implements CommandProcessor {

    @Override
    public ProcessResult process(Command command, UserState state) {
        return new ProcessResult(BotParameterProvider.START_RESPONSE);
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.START;
    }
}
