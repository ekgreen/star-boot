package com.github.old.dog.star.boot.transport.http.body;

/**
 * Marker interface representing an HTTP request body.
 * <p>
 * This interface serves as a common abstraction for different types of HTTP request bodies,
 * such as JSON payloads, form data, byte arrays, etc. Implementations of this interface
 * should provide appropriate methods to convert their content to a format suitable for
 * HTTP transmission.
 * <p>
 * The interface does not define any methods, as specific body handling is delegated to
 * the HTTP client implementation, which should recognize and process different body types
 * accordingly through pattern matching or type checking.
 * <p>
 * Example implementations might include JsonBody, FormBody, ByteArrayBody, etc.
 */
public interface Body {
}
