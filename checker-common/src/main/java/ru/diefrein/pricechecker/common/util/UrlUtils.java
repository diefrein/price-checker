package ru.diefrein.pricechecker.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
    public static final String URL_REGEX = "(https?://\\S+)";
    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    /**
     * Extract url from given text
     *
     * @param text text
     * @return url
     * @throws IllegalArgumentException if text doesn't match url pattern
     */
    public static String extractUrl(String text) {
        Matcher matcher = URL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Url cannot be extracted from text");
    }
}
