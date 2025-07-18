package ru.diefrein.pricechecker.service.impl;

import ru.diefrein.pricechecker.service.ProductParser;
import ru.diefrein.pricechecker.service.SiteParser;
import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.service.dto.enums.ProcessableSite;
import ru.diefrein.pricechecker.service.exception.PriceCheckerException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProductParserImpl implements ProductParser {

    private final Map<ProcessableSite, SiteParser> siteParses;

    public ProductParserImpl(Map<ProcessableSite, SiteParser> siteParses) {
        this.siteParses = siteParses;
    }

    @Override
    public ParsedProduct getProduct(String link) {
        ProcessableSite processableSite = getProcessableSiteFromLink(link);
        if (!siteParses.containsKey(processableSite)) {
            throw new PriceCheckerException(String.format("No parser for site with type=%s", processableSite));
        }
        return siteParses.get(processableSite).getProduct(link);
    }

    private ProcessableSite getProcessableSiteFromLink(String link) {
        List<ProcessableSite> processableSites = Arrays.stream(ProcessableSite.values())
                .filter(processableSite -> link.contains(processableSite.getDomainName()))
                .toList();
        if (processableSites.isEmpty()) {
            throw new PriceCheckerException(
                    String.format("Link=%s doesn't contain any processable sites", link)
            );
        }
        if (processableSites.size() > 1) {
            throw new PriceCheckerException(
                    String.format("Link=%s contains more than one processable sites", link)
            );
        }
        return processableSites.getFirst();
    }
}
