package ru.diefrein.pricechecker.bot.service;


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
}
