package ru.diefrein.pricechecker.bot.bot.commands.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;

import java.util.UUID;

public class RemoveSubscriptionProcessor implements CommandProcessor {

    private static final Logger log = LoggerFactory.getLogger(RemoveSubscriptionProcessor.class);

    private final SubscriptionService subscriptionService;

    public RemoveSubscriptionProcessor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        String productId = StringUtils.removeStart(
                command.callbackData(),
                ProcessableCommandType.REMOVE_SUBSCRIPTION.name().concat("_")
        );
        subscriptionService.remove(UUID.fromString(productId));
        return ProcessResult.toInitialState(BotParameterProvider.REMOVE_SUBSCRIPTION_RESPONSE);
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.REMOVE_SUBSCRIPTION;
    }
}
