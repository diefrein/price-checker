package ru.diefrein.pricechecker.bot.bot.commands.impl;

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
        PageRequest pageRequest = new PageRequest(BotParameterProvider.SUBSCRIPTIONS_PAGE_SIZE, 1);
        Page<UserSubscription> subscriptions = subscriptionService.getUserSubscriptions(command.chatId(), pageRequest);
        return ProcessResult.toInitialState(toResponse(subscriptions), mapToButtonLayout(subscriptions));
    }

    @Override
    public ProcessResult processCallback(Command command, UserState state) {
        PageRequest pageRequest;
        try {
            pageRequest = objectMapper.readValue(
                    StringUtils.removeStart(
                            command.callbackData(),
                            ProcessableCommandType.SUBSCRIPTIONS.name().concat("_")),
                    PageRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Page<UserSubscription> subscriptions = subscriptionService.getUserSubscriptions(command.chatId(), pageRequest);
        return ProcessResult.toInitialState(toResponse(subscriptions), mapToButtonLayout(subscriptions));
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
                    .formatted(i + 1, subscription.name());
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
        List<Button> buttons = new ArrayList<>();
        PageRequest currentPageRequest = subscriptions.meta().pageRequest();

        if (currentPageRequest.pageNumber() > 1) {
            buttons.add(getPrevButton(currentPageRequest));
        }

        if (!subscriptions.data().isEmpty()) {
            buttons.add(getNextButton(currentPageRequest));
        }
        return new ButtonLayout.ButtonRow(subscriptions.data().size(), buttons);
    }

    private Button getPrevButton(PageRequest pageRequest) {
        return getButton(
                new PageRequest(pageRequest.pageSize(), pageRequest.pageNumber() - 1),
                BotParameterProvider.PREV_BUTTON_SIGN + BotParameterProvider.PREV_BUTTON_DISPLAY_TEXT
        );
    }

    private Button getNextButton(PageRequest pageRequest) {
        return getButton(
                new PageRequest(pageRequest.pageSize(), pageRequest.pageNumber() + 1),
                BotParameterProvider.NEXT_BUTTON_DISPLAY_TEXT + BotParameterProvider.NEXT_BUTTON_SIGN
        );
    }

    private Button getButton(PageRequest newPageRequest, String displayText) {
        try {
            String pageRequest = objectMapper.writeValueAsString(newPageRequest);
            return new Button(
                    displayText,
                    ProcessableCommandType.SUBSCRIPTIONS.name().concat("_").concat(pageRequest)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
