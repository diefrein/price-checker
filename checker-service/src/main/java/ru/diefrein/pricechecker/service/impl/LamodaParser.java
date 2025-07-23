package ru.diefrein.pricechecker.service.impl;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.diefrein.pricechecker.service.SiteParser;
import ru.diefrein.pricechecker.service.dto.ParsedProduct;
import ru.diefrein.pricechecker.service.dto.enums.ProcessableSite;
import ru.diefrein.pricechecker.service.exception.PriceCheckerException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LamodaParser implements SiteParser {

    private static final Pattern PRICE_REGEX = Pattern.compile("\"price\"\\s*:\\s*\"?([0-9]+(?:\\.[0-9]+)?)\"?");

    @Override
    public ParsedProduct getProduct(String link) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)); // Use headless(true) if needed

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36")
                    .setViewportSize(1280, 800));

            Page page = context.newPage();

            String url = "https://www.lamoda.ru/p/mp002xm0d95h/shoes-thomasmunz-lofery/";
            page.navigate(url);

            page.waitForSelector("span.x-product-price__value");

            String name = page.textContent("h1");
            String price = page.textContent("span.x-product-price__value");

            System.out.println("Product: " + name);
            System.out.println("Price: " + price);

            return new ParsedProduct(name, Double.parseDouble(price));
        }
    }

    @Override
    public ProcessableSite getProcessableSite() {
        return ProcessableSite.LAMODA;
    }

    private String getPrice(Document doc, String link) {
        Elements scripts = doc.select("script[type=application/ld+json]");
        String price = null;
        for (Element el : scripts) {
            String json = el.html();
            if (json.contains("\"offers\"")) {
                Matcher m = PRICE_REGEX.matcher(json);
                if (m.find()) {
                    price = m.group(1);
                    break;
                }
            }
        }

        if (price == null) {
            Element metaPrice = doc.selectFirst("meta[itemprop=price]");
            if (metaPrice != null) {
                price = metaPrice.attr("content");
            }
        }

        if (price == null) {
            throw new PriceCheckerException(
                    String.format("Price not found for product with link=%s", link)
            );
        }
        return price;
    }
}
