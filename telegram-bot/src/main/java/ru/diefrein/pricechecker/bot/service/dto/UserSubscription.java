package ru.diefrein.pricechecker.bot.service.dto;

import java.util.UUID;

public record UserSubscription(UUID checkerProductId,
                               String link,
                               String name,
                               Double actualPrice) {
}
