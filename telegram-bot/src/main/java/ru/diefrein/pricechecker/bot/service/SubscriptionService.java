package ru.diefrein.pricechecker.bot.service;


import ru.diefrein.pricechecker.bot.service.dto.UserSubscription;

import java.util.List;

/**
 * Service that handles user's subscriptions
 */
public interface SubscriptionService {

    /**
     * Subscribe to updates
     *
     * @param telegramId id of chat with user
     * @param link link to the product which will be followed
     */
    void subscribe(long telegramId, String link);

    /**
     * Get list of all user's subscriptions
     *
     * @param telegramId id of chat with user
     * @return user subscriptions
     */
    List<UserSubscription> getUserSubscriptions(long telegramId);
}
