package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;

public class StartProcessor implements CommandProcessor {
    private static final String START_RESPONSE = """
            Welcome to Price Checker Bot!
            Type /register to create a profile
            Type /subscribe to subscribe for price updates
            """;

    @Override
    public ProcessResult process(Command command, UserState state) {
        return new ProcessResult(START_RESPONSE);
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.START;
    }
}
