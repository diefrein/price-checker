package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.service.dto.ParsedProduct;

/**
 * Service that parses specified site to retrieve product info
 */
public interface SiteParser {

    /**
     * Parse product page
     *
     * @param link link to page
     * @return product's information
     */
    ParsedProduct getProduct(String link);
}
