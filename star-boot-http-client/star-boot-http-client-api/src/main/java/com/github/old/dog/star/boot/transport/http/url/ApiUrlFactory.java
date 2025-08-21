package com.github.old.dog.star.boot.transport.http.url;


import com.github.old.dog.star.boot.interfaces.Key;

/**
 * Factory interface responsible for retrieving URLs based on unique keys.
 * <p>
 * This interface provides a way to decouple URL retrieval from service components,
 * allowing for centralized URL management and dynamic URL resolution at runtime.
 * Implementations can use various strategies such as configuration files, databases,
 * or in-memory maps to resolve keys to their corresponding URLs.
 * <p>
 * Typically used in HTTP client components to retrieve endpoint URLs without hardcoding
 * them in the business logic code.
 */
public interface ApiUrlFactory {
    /**
     * Retrieves a URL string associated with the specified key.
     * <p>
     * The key typically represents a logical endpoint name or identifier, which
     * is resolved to a concrete URL by the factory implementation.
     *
     * @param key The key object that identifies the desired URL
     * @return The URL string corresponding to the provided key
     * @throws IllegalArgumentException if the key is not recognized or cannot be resolved
     */
    String getUrl(Key key);
}
