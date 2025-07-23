package ru.diefrein.pricechecker.bot.bot.commands.impl;

import ru.diefrein.pricechecker.bot.bot.commands.Command;
import ru.diefrein.pricechecker.bot.bot.commands.CommandProcessor;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessResult;
import ru.diefrein.pricechecker.bot.bot.commands.ProcessableCommandType;
import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.service.ProductService;

public class SubscribeProcessor implements CommandProcessor {
    private static final String SUBSCRIBE_INITIAL_RESPONSE = "Type link in message below";
    private static final String SUBSCRIBE_WAIT_FOR_LINK_RESPONSE = "Product saved successfully!";
    private static final String SUBSCRIBE_WAIT_FOR_LINK_ERROR_RESPONSE =
            "Error while saving product, please retry request later";

    private final ProductService productService;

    public SubscribeProcessor(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ProcessResult process(Command command, UserState state) {
        switch (state) {
            case INITIAL -> {
                return new ProcessResult(SUBSCRIBE_INITIAL_RESPONSE);
            }
            case SUBSCRIBE_WAIT_FOR_LINK -> {
                return processWaitForLink(command);
            }
            default -> throw new IllegalStateException(String.format(
                    "UserState=%s is not processable by processor=%s", state, this.getClass().getSimpleName()
            ));
        }
    }

    @Override
    public ProcessableCommandType getProcessableCommandType() {
        return ProcessableCommandType.SUBSCRIBE;
    }

    private ProcessResult processWaitForLink(Command command) {
        String productLink = command.text().trim();
        try {
            productService.create(command.chatId(), productLink);
        } catch (Exception e) {
            return new ProcessResult(SUBSCRIBE_WAIT_FOR_LINK_ERROR_RESPONSE);
        }
        return new ProcessResult(SUBSCRIBE_WAIT_FOR_LINK_RESPONSE);
    }
}
