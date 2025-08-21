package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.bot.service.UserService;

public class CheckSubscriptionUpdatesProcessor implements CommandProcessor {

    private final SubscriptionService subscriptionService;

    public CheckSubscriptionUpdatesProcessor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        subscriptionService.checkForUpdatesAsync(command.chatId());
        return ProcessResult.toInitialState(BotParameterProvider.CHECK_SUBSCRIPTION_UPDATES_RESPONSE);
    }

}