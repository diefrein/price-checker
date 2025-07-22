package ru.diefrein.pricechecker.bot.transport.kafka.consumer;

import ru.diefrein.pricechecker.bot.PriceCheckerBot;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.transport.kafka.dto.PriceChangeEvent;

public class PriceChangeProcessor {

    private final PriceCheckerBot bot;
    private final UserService userService;

    public PriceChangeProcessor(PriceCheckerBot bot,
                                UserService userService) {
        this.bot = bot;
        this.userService = userService;
    }

    void process(PriceChangeEvent event) {
        User user = userService.findByCheckerUserId(event.userId());
        bot.sendMessage(user.telegramId(), buildResponse(event));
    }

    private String buildResponse(PriceChangeEvent event) {
        return String.format("Received an update on %s: now price is %s, was: %s\nlink: %s",
                event.productName(), event.newPrice(), event.oldPrice(), event.link());
    }
}
