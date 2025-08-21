package com.github.old.dog.star.boot.interfaces;

/**
 * Represents the `Api` interface, which extends the `Key` interface.
 * This interface provides a method to retrieve the URL associated with an API endpoint.
 * <p>
 * Classes or enums implementing the `Api` interface are expected to define API behavior,
 * which includes providing a unique key and constructing the URL for the corresponding endpoint.
 */
public interface Api extends Key {

    /**
     * Retrieves the URL associated with this API endpoint.
     *
     * @return a string representation of the URL
     */
    String getUrl();
}
