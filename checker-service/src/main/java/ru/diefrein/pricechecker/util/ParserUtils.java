package ru.diefrein.pricechecker.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.diefrein.pricechecker.configuration.parameters.ParserParameterProvider;

import java.io.IOException;

public class ParserUtils {

    public static Document getDocument(String link) {
        Document doc;
        try {
            doc = Jsoup.connect(link)
                    .userAgent(ParserParameterProvider.DEFAULT_USER_AGENT)
                    .timeout(ParserParameterProvider.REQUEST_TIMEOUT_MS)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }
}
