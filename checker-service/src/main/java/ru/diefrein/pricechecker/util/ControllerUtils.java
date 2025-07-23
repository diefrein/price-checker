package ru.diefrein.pricechecker.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ControllerUtils {

    public static final String POST = "POST";
    public static final String PATCH = "PATCH";
    public static final String GET = "GET";

    public static String getPathAfterPrefix(String path, String prefix) {
        int index = path.indexOf(prefix);
        if (index == -1) {
            throw new IllegalStateException(String.format("Path=%s doesn't contain prefix=%s", path, prefix));
        }
        return path.substring(index + prefix.length());
    }

    /**
     * Get query params as map
     *
     * @param query query
     * @return params map
     */
    public static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
            params.put(key, value);
        }

        return params;
    }

}
