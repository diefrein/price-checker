package ru.diefrein.pricechecker.bot.service.impl;

import ru.diefrein.pricechecker.bot.service.SubscriptionService;
import ru.diefrein.pricechecker.bot.service.dto.UserSubscription;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.common.client.CheckerServiceClient;
import ru.diefrein.pricechecker.common.client.dto.CheckerProduct;
import ru.diefrein.pricechecker.common.client.dto.CreateCheckerProductRequest;
import ru.diefrein.pricechecker.common.storage.dto.Page;
import ru.diefrein.pricechecker.common.storage.dto.PageRequest;

import java.util.UUID;

public class SubscriptionServiceImpl implements SubscriptionService {

    private final CheckerServiceClient checkerServiceClient;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(CheckerServiceClient checkerServiceClient, UserRepository userRepository) {
        this.checkerServiceClient = checkerServiceClient;
        this.userRepository = userRepository;
    }

    @Override
    public void subscribe(long chatId, String link) {
        User user = userRepository.findByTelegramId(chatId);
        checkerServiceClient.createProduct(new CreateCheckerProductRequest(user.checkerUserId(), link));
    }

    @Override
    public Page<UserSubscription> getUserSubscriptions(long chatId, PageRequest pageRequest) {
        User user = userRepository.findByTelegramId(chatId);
        Page<CheckerProduct> products = checkerServiceClient.getUserProducts(user.checkerUserId(), pageRequest);
        return new Page<>(products.data().stream().map(this::map).toList(), products.meta());
    }

    @Override
    public void remove(UUID productId) {
        checkerServiceClient.removeProduct(productId);
    }

    private UserSubscription map(CheckerProduct product) {
        return new UserSubscription(
                product.id(),
                product.link(),
                removeSpecialChars(product.name()),
                product.actualPrice()
        );
    }

    private String removeSpecialChars(String str) {
        return str.replace("*", "");
    }
}
