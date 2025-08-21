package com.github.old.dog.star.boot.transport.http.call;

import java.util.HashMap;
import java.util.Map;
import com.github.old.dog.star.boot.transport.http.body.Body;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Represents an HTTP POST request configuration.
 * <p>
 * This class encapsulates all the necessary information for executing an HTTP POST request,
 * including the target URL, request headers, query parameters, and request body.
 * It uses the Builder pattern to provide a fluent and type-safe way to construct
 * POST request configurations.
 * <p>
 * Example usage:
 * <pre>
 * Post post = Post.builder("https://api.example.com/resource")
 *     .addQueryParam("param1", "value1")
 *     .withHeader("Content-Type", "application/json")
 *     .withPayload(new JsonBody("{\"key\":\"value\"}"))
 *     .build();
 * </pre>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {

    /**
     * The target URL for the POST request.
     */
    private String url;

    /**
     * The request body to be sent with the POST request.
     */
    private Body body;

    /**
     * HTTP headers to be included with the request.
     */
    private Map<String, String> headers;

    /**
     * Query parameters to be appended to the URL.
     */
    private Map<String, Object> queryParams;

    /**
     * Creates a new builder for constructing a POST request.
     *
     * @param url the target URL for the POST request
     * @return a new Builder instance initialized with the specified URL
     */
    public static Builder builder(String url) {
        return new Builder(url);
    }

    /**
     * Builder class for constructing POST request configurations.
     * <p>
     * This class implements the Builder pattern to provide a fluent API for
     * constructing POST request configurations with various options.
     */
    @RequiredArgsConstructor
    public static class Builder {
        /**
         * Query parameters to be appended to the URL.
         */
        private final Map<String, Object> queryParams = new HashMap<>();

        /**
         * HTTP headers to be included with the request.
         */
        private final Map<String, String> headers = new HashMap<>();

        /**
         * The target URL for the POST request.
         */
        private final String url;

        /**
         * The request body to be sent with the POST request.
         */
        private Body body;

        /**
         * Adds a single query parameter to the request.
         *
         * @param key the name of the query parameter
         * @param val the value of the query parameter
         * @return this builder instance for method chaining
         */
        public Builder addQueryParam(String key, Object val) {
            this.queryParams.put(key, val);
            return this;
        }

        /**
         * Adds multiple query parameters to the request.
         *
         * @param queryParams a map of query parameter names to values
         * @return this builder instance for method chaining
         */
        public Builder withQueryParams(Map<String, Object> queryParams) {
            this.queryParams.putAll(queryParams);
            return this;
        }

        /**
         * Adds a single HTTP header to the request.
         *
         * @param name  the name of the HTTP header
         * @param value the value of the HTTP header
         * @return this builder instance for method chaining
         */
        public Builder withHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        /**
         * Adds multiple HTTP headers to the request.
         *
         * @param headers a map of HTTP header names to values
         * @return this builder instance for method chaining
         */
        public Builder withHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder withPayload(Body body) {
            this.body = body;
            return this;
        }

        /**
         * Builds a new Post instance with the configuration specified in this builder.
         *
         * @return a new Post instance with the specified configuration
         */
        public Post build() {
            final Post post = new Post();
            post.url = url;
            post.queryParams = queryParams;
            post.headers = headers;
            post.body = body;

            return post;
        }

    }
}
