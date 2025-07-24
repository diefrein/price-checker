package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.bot.service.dto.UserSubscription;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionsProcessor implements CommandProcessor {

    private final SubscriptionService subscriptionService;

    public SubscriptionsProcessor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        List<UserSubscription> subscriptions = subscriptionService.getUserSubscriptions(command.chatId());
        return ProcessResult.toInitialState(toResponse(subscriptions));
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.SUBSCRIPTIONS;
    }

    private String toResponse(List<UserSubscription> subscriptions) {
        if (subscriptions.isEmpty()) {
            return BotParameterProvider.SUBSCRIPTIONS_NO_SUBSCRIPTIONS_FOUND_RESPONSE;
        }
        String subscriptionsStr = subscriptions.stream().map(this::mapToTemplate).collect(Collectors.joining("\n\n"));
        return BotParameterProvider.SUBSCRIPTIONS_SUBSCRIPTIONS_FOUND_RESPONSE.concat(subscriptionsStr);
    }

    private String mapToTemplate(UserSubscription subscription) {
        return BotParameterProvider.SUBSCRIPTIONS_SUBSCRIPTION_TEMPLATE
                .formatted(subscription.name(), subscription.actualPrice(), subscription.link());
    }
}
