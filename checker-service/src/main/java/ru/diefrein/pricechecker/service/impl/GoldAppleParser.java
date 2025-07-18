package ru.diefrein.pricechecker.service.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.diefrein.pricechecker.service.SiteParser;
import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.service.dto.enums.ProcessableSite;
import ru.diefrein.pricechecker.service.exception.PriceCheckerException;
import ru.diefrein.pricechecker.util.ParserUtils;

public class GoldAppleParser implements SiteParser {

    @Override
    public ParsedProduct getProduct(String link) {
        Document doc = ParserUtils.getDocument(link);

        String name = getName(doc);
        String price = getPrice(doc, link);

        return new ParsedProduct(name, Double.parseDouble(price));
    }

    @Override
    public ProcessableSite getProcessableSite() {
        return ProcessableSite.GOLD_APPLE;
    }

    private String getName(Document doc) {
        Element nameEl = doc.selectFirst("h1, h1.product-name, h1.product-title");
        return nameEl != null ? nameEl.text().trim() : null;
    }

    private String getPrice(Document doc, String link) {
        Element priceMeta = doc.selectFirst("meta[itemprop=price]");
        String price = priceMeta != null ? priceMeta.attr("content") : null;
        if (price == null) {
            throw new PriceCheckerException(
                    String.format("Price not found for product with link=%s", link)
            );
        }

        return price;
    }

}
