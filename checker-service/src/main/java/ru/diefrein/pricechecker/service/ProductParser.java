package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.service.dto.ParsedProduct;

/**
 * Service that parses page and retrieves product's information
 */
public interface ProductParser {

    /**
     * Parse page
     *
     * @param link link to page
     * @return product's information
     */
    ParsedProduct getProduct(String link);
}
