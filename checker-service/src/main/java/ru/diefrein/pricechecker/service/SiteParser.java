package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.service.dto.enums.ProcessableSite;

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

    /**
     * @return site that is parsable by that class
     */
    ProcessableSite getProcessableSite();
}
