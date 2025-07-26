package ru.diefrein.pricechecker.bot.service;


import ru.diefrein.pricechecker.bot.service.dto.UserSubscription;
import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service that handles user's subscriptions
 */
public interface SubscriptionService {

    /**
     * Subscribe to updates
     *
     * @param telegramId id of chat with user
     * @param link       link to the product which will be followed
     */
    void subscribe(long telegramId, String link);

    /**
     * Get page of user's subscriptions
     *
     * @param telegramId id of chat with user
     * @param pageRequest pagination parameters
     * @return page of user's subscriptions
     */
    Page<UserSubscription> getUserSubscriptions(long telegramId, PageRequest pageRequest);

    /**
     * Remove subscription
     *
     * @param productId id of product in checker-db
     */
    void remove(UUID productId);
}
