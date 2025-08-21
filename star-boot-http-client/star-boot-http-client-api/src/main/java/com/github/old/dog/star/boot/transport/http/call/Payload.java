package com.github.old.dog.star.boot.transport.http.call;

import com.github.old.dog.star.boot.interfaces.Converter;
import java.util.List;

/**
 * Interface representing an HTTP response payload.
 * <p>
 * This interface provides methods to access various components of an HTTP response,
 * including headers, cookies, and the response body. It also provides conversion
 * utilities to transform the raw response body into various data types using
 * converter functions.
 * <p>
 * The interface is organized into sections for clarity:
 * - Main methods: Core functionality for accessing response components
 * - Compose methods: Utility methods for derived response properties
 * - Default methods: Convenience methods with default implementations
 */
public interface Payload {
    // ============================= Main Methods ============================== //

    /**
     * Retrieves the value of the specified HTTP header from the response.
     *
     * @param header the name of the header to retrieve
     * @return the value of the header, or null if the header is not present
     */
    String header(String header);

    /**
     * Retrieves the value of the specified cookie from the response.
     *
     * @param name the name of the cookie to retrieve
     * @return the value of the cookie, or null if the cookie is not present
     */
    String cookie(String name);

    /**
     * Converts the response body to the specified type using the provided converter.
     * <p>
     * This method allows for flexible transformation of the raw response body into
     * various data structures or domain objects based on the converter provided.
     *
     * @param <R> the target type of the conversion
     * @param converter the converter function to transform the raw byte array to the target type
     * @return the converted object of type R
     * @throws RuntimeException if the conversion fails
     */
    <R> R convert(Converter<byte[], R> converter);

    // ============================ Compose Methods ============================ //
    /**
     * Returns the length of the response body in bytes.
     *
     * @return the content length in bytes, or -1 if unknown
     */
    long contentLength();

    /**
     * Returns all cookies from the response as a single string.
     * <p>
     * The format of the returned string is typically a semicolon-separated list
     * of name-value pairs, suitable for use in a Cookie header for subsequent requests.
     *
     * @return a string containing all cookies, or an empty string if no cookies are present
     */
    String cookies();

    // ============================ Default Methods ============================ //
    /**
     * Retrieves all values for the specified HTTP header from the response.
     * <p>
     * This default implementation assumes that headers have at most one value,
     * wrapping the result of {@link #header(String)} in a singleton list.
     * Implementations should override this method if they need to support
     * multiple values for a single header name.
     *
     * @param header the name of the header to retrieve
     * @return a list containing all values for the header, or an empty list if the header is not present
     */
    default List<String> headers(String header) {
        return List.of(header(header));
    }
}
