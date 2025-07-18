package ru.diefrein.pricechecker.service;

import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.service.dto.enums.ProcessableSite;

public interface SiteParser {

    ParsedProduct getProduct(String link);

    ProcessableSite getProcessableSite();
}
