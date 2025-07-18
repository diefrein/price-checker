package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.service.dto.ParsedProduct;

public interface ProductParser {

    ParsedProduct getProduct(String link);
}
