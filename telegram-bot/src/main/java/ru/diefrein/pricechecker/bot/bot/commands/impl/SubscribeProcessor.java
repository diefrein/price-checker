package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.common.util.UrlUtils;

public class SubscribeProcessor implements CommandProcessor {

    private final SubscriptionService subscriptionService;

    public SubscribeProcessor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        switch (state) {
            case INITIAL -> {
                return new ProcessResult(
                        BotParameterProvider.SUBSCRIBE_INITIAL_RESPONSE,
                        UserState.SUBSCRIBE_WAIT_FOR_LINK,
                        null
                );
            }
            case SUBSCRIBE_WAIT_FOR_LINK -> {
                return processWaitForLink(command);
            }
            default -> throw new IllegalStateException(String.format(
                    "UserState=%s is not processable by processor=%s", state, this.getClass().getSimpleName()
            ));
        }
    }

    private ProcessResult processWaitForLink(Command command) {
        String productLink = command.text().trim();
        subscriptionService.subscribe(command.chatId(), UrlUtils.extractUrl(productLink));
        return ProcessResult.toInitialState(BotParameterProvider.SUBSCRIBE_WAIT_FOR_LINK_RESPONSE);
    }
}
