package ru.diefrein.pricechecker.bot.transport.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.diefrein.pricechecker.bot.bot.PriceCheckerBot;
import ru.diefrein.pricechecker.bot.configuration.parameters.BotParameterProvider;
import ru.diefrein.pricechecker.bot.service.UserService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.transport.kafka.dto.PriceChangeEvent;

public class PriceChangeProcessor {

    private static final Logger log = LoggerFactory.getLogger(PriceChangeProcessor.class);

    private final PriceCheckerBot bot;
    private final UserService userService;

    public PriceChangeProcessor(PriceCheckerBot bot,
                                UserService userService) {
        this.bot = bot;
        this.userService = userService;
    }

    void process(PriceChangeEvent event) {
        log.info("Received PriceChangeEvent={}", event);
        if (!event.newPrice().equals(event.oldPrice())) {
            User user = userService.findByCheckerUserId(event.userId());
            bot.sendMessage(user.telegramId(), buildResponse(event));
        }
    }

    private String buildResponse(PriceChangeEvent event) {
        String priceDirection = event.newPrice() < event.oldPrice()
                ? BotParameterProvider.PRICE_DOWN_SIGN
                : BotParameterProvider.PRICE_UP_SIGN;
        String priceUpdateResponse = BotParameterProvider.PRICE_UPDATE_RESPONSE
                .formatted(event.productName(), event.newPrice(), event.oldPrice(), event.link());
        return "%s %s".formatted(priceDirection, priceUpdateResponse);
    }
}
