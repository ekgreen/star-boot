package com.github.old.dog.star.boot.transport.http.url;

import com.github.old.dog.star.boot.interfaces.Api;
import com.github.old.dog.star.boot.interfaces.Key;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link ApiUrlFactory} interface for managing and retrieving
 * URLs required by HTTP clients. This class provides a centralized mechanism
 * to store and retrieve URLs based on identifier keys.
 * <p>
 * The URLs are stored internally in a map, and the class offers functionality
 * to register URLs either directly using a key-value pair or through an {@link Api} object.
 * <p>
 * Methods:
 * - Allows validation of URLs upon registration to ensure correctness.
 * - Provides URL retrieval based on keys.
 * <p>
 * Thread Safety:
 * This implementation does not guarantee thread safety. If multiple threads
 * access an instance of {@code HttpClientsUrlFactory} concurrently and modify the URLs,
 * external synchronization or a thread-safe wrapper should be used.
 * <p>
 * Usage Scenarios:
 * - Useful for dynamically configuring endpoints in HTTP clients at runtime.
 * - Decouples URL management from application logic by centralizing URL-related configurations.
 */
public class HttpClientsUrlFactory implements ApiUrlFactory {

    private final Map<String, String> urls = new HashMap<>();

    @Override
    public String getUrl(Key key) {
        return urls.get(key.getKey());
    }

    public HttpClientsUrlFactory registry(String key, String url) {
        if (isValidUrl(url)) {
            urls.put(key, url);
        }
        return this;
    }

    public HttpClientsUrlFactory registry(Api api) {
        return registry(api.getKey(), api.getUrl());
    }

    /**
     * Проверяет, является ли строка валидным URL
     *
     * @param url строка для проверки
     * @return true если URL валиден, false в противном случае
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        try {
            URI uri = new URI(url);

            // Проверяем наличие схемы (протокола)
            if (uri.getScheme() == null) {
                return false;
            }

            // Проверяем, что протокол http или https
            if (!uri.getScheme().equalsIgnoreCase("http")
                && !uri.getScheme().equalsIgnoreCase("https")) {
                return false;
            }

            // Проверяем наличие хоста
            if (uri.getHost() == null || uri.getHost().isEmpty()) {
                return false;
            }

            // Проверяем, что порт в допустимом диапазоне (если указан)
            if (uri.getPort() != -1 && (uri.getPort() < 1 || uri.getPort() > 65535)) {
                return false;
            }

            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
