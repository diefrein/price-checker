package ru.diefrein.pricechecker.bot.service.impl;

import ru.diefrein.pricechecker.bot.service.ProductService;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;
import ru.diefrein.pricechecker.bot.transport.client.CheckerServiceClient;
import ru.diefrein.pricechecker.bot.transport.client.dto.CreateCheckerProductRequest;

public class ProductServiceImpl implements ProductService {

    private final CheckerServiceClient checkerServiceClient;
    private final UserRepository userRepository;

    public ProductServiceImpl(CheckerServiceClient checkerServiceClient, UserRepository userRepository) {
        this.checkerServiceClient = checkerServiceClient;
        this.userRepository = userRepository;
    }

    @Override
    public void create(long telegramId, String link) {
        User user = userRepository.findByTelegramId(telegramId);
        checkerServiceClient.createProduct(new CreateCheckerProductRequest(user.checkerUserId(), link));
    }
}
