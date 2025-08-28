package ru.diefrein.pricechecker.bot.bot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;

import java.util.UUID;

public class RemoveSubscriptionProcessor implements CommandProcessor {

    private final SubscriptionService subscriptionService;
    private final SubscriptionsProcessor subscriptionsProcessor;

    public RemoveSubscriptionProcessor(SubscriptionService subscriptionService,
                                       SubscriptionsProcessor subscriptionsProcessor) {
        this.subscriptionService = subscriptionService;
        this.subscriptionsProcessor = subscriptionsProcessor;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        throw new UnsupportedOperationException("Method is not implemented for non-callback commands");
    }

    @Override
    public ProcessResult processCallback(Command command, UserState state) {
        String productId = StringUtils.removeStart(
                command.callbackData(),
                ProcessableCommandType.REMOVE_SUBSCRIPTION.name().concat("_")
        );
        subscriptionService.remove(UUID.fromString(productId));

        // todo temporary solution - refactor to ChainedCommandProcessor to execute multiple commands per message
        return subscriptionsProcessor.process(command, state);
    }
}
