package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.UserService;

public class RegisterProcessor implements CommandProcessor {

    private final UserService userService;

    public RegisterProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        userService.create(command.chatId(), command.username());
        return new ProcessResult(BotParameterProvider.REGISTER_RESPONSE);
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.REGISTER;
    }
}
