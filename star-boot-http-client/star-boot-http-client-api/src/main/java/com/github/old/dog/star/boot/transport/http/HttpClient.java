package com.github.old.dog.star.boot.transport.http;

import com.github.old.dog.star.boot.transport.http.call.Get;
import com.github.old.dog.star.boot.transport.http.call.Payload;
import com.github.old.dog.star.boot.transport.http.call.Post;

/**
 * Interface defining a simple HTTP client with support for basic HTTP operations.
 * <p>
 * This interface provides methods for executing HTTP requests using different HTTP methods
 * such as GET and POST. It abstracts away the underlying HTTP client implementation details,
 * allowing for easy substitution of different HTTP client libraries or implementations.
 * <p>
 * Implementations of this interface should handle common HTTP client concerns such as
 * connection pooling, timeouts, and error handling according to the requirements of
 * the specific application context.
 */
public interface HttpClient {
    /**
     * Executes an HTTP POST request.
     * <p>
     * This method sends data to a specified URL using the HTTP POST method. It's typically
     * used for creating resources, submitting form data, or sending data to be processed.
     *
     * @param post The POST request configuration containing URL, headers, and body
     * @return A Payload object containing the HTTP response
     * @throws RuntimeException if an error occurs during the HTTP request execution
     */
    Payload post(Post post);

    /**
     * Executes an HTTP GET request.
     * <p>
     * This method retrieves data from a specified URL using the HTTP GET method. It's typically
     * used for fetching resources or data from a server without modifying server state.
     *
     * @param get The GET request configuration containing URL, headers, and query parameters
     * @return A Payload object containing the HTTP response
     * @throws RuntimeException if an error occurs during the HTTP request execution
     */
    Payload get(Get get);
}
