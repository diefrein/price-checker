package ru.diefrein.pricechecker.service.impl;

import ch.qos.logback.core.util.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.diefrein.pricechecker.service.SiteParser;
import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.service.exception.PriceCheckerException;
import ru.diefrein.pricechecker.util.ParserUtils;

public class Store77Parser implements SiteParser {

    @Override
    public ParsedProduct getProduct(String link) {
        Document doc = ParserUtils.getDocument(link);

        String name = getName(doc);
        String price = getPrice(doc, link);

        return new ParsedProduct(name, Double.parseDouble(price));
    }

    private String getName(Document doc) {
        Element titleEl = doc.selectFirst("h1, h2, .product-title, .product-name");
        return titleEl != null ? titleEl.text().trim() : null;
    }

    private String getPrice(Document doc, String link) {
        Element priceEl = doc.selectFirst("p.price_title_product");
        if (priceEl == null || StringUtil.isNullOrEmpty(priceEl.text())) {
            throw new PriceCheckerException(
                    String.format("Price not found for product with link=%s", link)
            );
        }
        String rawPrice = priceEl.text();
        return rawPrice.replaceAll("[^0-9]", "");
    }

}
