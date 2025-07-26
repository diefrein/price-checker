package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import ru.diefrein.pricechecker.bot.bot.buttons.Button;
import ru.diefrein.pricechecker.bot.bot.buttons.ButtonLayout;
import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.bot.service.dto.UserSubscription;
import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionsProcessor implements CommandProcessor {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    public SubscriptionsProcessor(SubscriptionService subscriptionService,
                                  ObjectMapper objectMapper) {
        this.subscriptionService = subscriptionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        PageRequest pageRequest = new PageRequest(5, 1);
        if (!StringUtil.isNullOrEmpty(command.callbackData())) {
            try {
                pageRequest = objectMapper.readValue(
                        StringUtils.removeStart(
                                command.callbackData(),
                                ProcessableCommandType.SUBSCRIPTIONS.name().concat("_")),
                        PageRequest.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        Page<UserSubscription> subscriptions = subscriptionService.getUserSubscriptions(command.chatId(), pageRequest);
        return ProcessResult.toInitialState(toResponse(subscriptions), mapToButtonLayout(subscriptions));
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.SUBSCRIPTIONS;
    }

    private String toResponse(Page<UserSubscription> subscriptions) {
        if (subscriptions.data().isEmpty()) {
            return BotParameterProvider.SUBSCRIPTIONS_NO_SUBSCRIPTIONS_FOUND_RESPONSE;
        }
        String subscriptionsStr = subscriptions.data().stream()
                .map(this::mapToTemplate)
                .collect(Collectors.joining("\n\n"));
        return BotParameterProvider.SUBSCRIPTIONS_SUBSCRIPTIONS_FOUND_RESPONSE.concat(subscriptionsStr);
    }

    private String mapToTemplate(UserSubscription subscription) {
        return BotParameterProvider.SUBSCRIPTIONS_SUBSCRIPTION_TEMPLATE
                .formatted(subscription.name(), subscription.actualPrice(), subscription.link());
    }

    private ButtonLayout mapToButtonLayout(Page<UserSubscription> subscriptions) {
        List<ButtonLayout.ButtonRow> buttonRows = new ArrayList<>();

        for (int i = 0; i < subscriptions.data().size(); i++) {
            UserSubscription subscription = subscriptions.data().get(i);
            String displayText = BotParameterProvider.SUBSCRIPTIONS_SUBSCRIPTION_BUTTON_TEMPLATE
                    .formatted(i, subscription.name());
            String callbackData = ProcessableCommandType.REMOVE_SUBSCRIPTION.name()
                    .concat("_")
                    .concat(subscription.checkerProductId().toString());
            Button button = new Button(displayText, callbackData);
            buttonRows.add(new ButtonLayout.ButtonRow(i, List.of(button)));
        }

        ButtonLayout.ButtonRow paginationRow;
        paginationRow = getPaginationButtons(subscriptions);
        buttonRows.add(paginationRow);
        return new ButtonLayout(buttonRows);
    }

    private ButtonLayout.ButtonRow getPaginationButtons(Page<UserSubscription> subscriptions) {
        if (subscriptions.data().isEmpty()) {
            return null;
        }

        String pageRequestPrev = null;
        try {
            pageRequestPrev = objectMapper.writeValueAsString(new PageRequest(
                    subscriptions.meta().pageRequest().pageSize(),
                    subscriptions.meta().pageRequest().pageNumber() - 1
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String pageRequestNext = null;
        try {
            pageRequestNext = objectMapper.writeValueAsString(new PageRequest(
                    subscriptions.meta().pageRequest().pageSize(),
                    subscriptions.meta().pageRequest().pageNumber() + 1
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Button prevBtn = new Button(
                "⬅️ Prev",
                ProcessableCommandType.SUBSCRIPTIONS.name().concat("_").concat(pageRequestPrev)
        );
        Button nextBtn = new Button(
                "Next ➡️",
                ProcessableCommandType.SUBSCRIPTIONS.name().concat("_").concat(pageRequestNext)
        );
        return new ButtonLayout.ButtonRow(subscriptions.data().size(), List.of(prevBtn, nextBtn));
    }
}
