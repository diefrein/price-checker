package ru.diefrein.pricechecker.transport.kafka.dto;

import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.storage.entity.Product;

import java.util.UUID;

public record PriceChangeEvent(UUID userId, String productName, String link, Double newPrice, Double oldPrice) {

    public static PriceChangeEvent fromProductUpdate(Product oldProduct, ParsedProduct newProduct) {
        return new PriceChangeEvent(
                oldProduct.userId(),
                newProduct.name(),
                oldProduct.link(),
                newProduct.actualPrice(),
                oldProduct.actualPrice()
        );
    }
}
