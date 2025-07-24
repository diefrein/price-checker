package ru.diefrein.pricechecker.bot.service.impl;

import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.bot.service.dto.UserSubscription;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.bot.transport.http.client.CheckerServiceClient;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CheckerProduct;
import ru.diefrein.pricechecker.bot.transport.http.client.dto.CreateCheckerProductRequest;

import java.util.List;

public class SubscriptionServiceImpl implements SubscriptionService {

    private final CheckerServiceClient checkerServiceClient;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(CheckerServiceClient checkerServiceClient, UserRepository userRepository) {
        this.checkerServiceClient = checkerServiceClient;
        this.userRepository = userRepository;
    }

    @Override
    public void subscribe(long telegramId, String link) {
        User user = userRepository.findByTelegramId(telegramId);
        checkerServiceClient.createProduct(new CreateCheckerProductRequest(user.checkerUserId(), link));
    }

    @Override
    public List<UserSubscription> getUserSubscriptions(long telegramId) {
        User user = userRepository.findByTelegramId(telegramId);
        List<CheckerProduct> products = checkerServiceClient.getUserProducts(user.checkerUserId());
        return products.stream().map(this::map).toList();
    }

    private UserSubscription map(CheckerProduct product) {
        return new UserSubscription(
                product.link(),
                product.name(),
                product.actualPrice()
        );
    }
}
