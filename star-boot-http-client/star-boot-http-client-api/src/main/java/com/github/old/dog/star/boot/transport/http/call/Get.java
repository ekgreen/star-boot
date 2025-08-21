package com.github.old.dog.star.boot.transport.http.call;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP GET request configuration.
 * <p>
 * This class encapsulates all the necessary information for executing an HTTP GET request,
 * including the target URL, request headers, and query parameters. It uses the Builder
 * pattern to provide a fluent and type-safe way to construct GET request configurations.
 * <p>
 * Example usage:
 * <pre>
 * Get get = Get.builder("https://api.example.com/resource")
 *     .addQueryParam("param1", "value1")
 *     .withHeader("Accept", "application/json")
 *     .build();
 * </pre>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Get {

    /**
     * The target URL for the GET request.
     */
    private String url;

    /**
     * HTTP headers to be included with the request.
     */
    private Map<String, String> headers;

    /**
     * Query parameters to be appended to the URL.
     */
    private Map<String, Object> queryParams;

    /**
     * Creates a new builder for constructing a GET request.
     *
     * @param url the target URL for the GET request
     * @return a new Builder instance initialized with the specified URL
     */
    public static Builder builder(String url) {
        return new Builder(url);
    }

    /**
     * Builder class for constructing GET request configurations.
     * <p>
     * This class implements the Builder pattern to provide a fluent API for
     * constructing GET request configurations with various options.
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
         * The target URL for the GET request.
         */
        private final String url;

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

        /**
         * Builds a new Get instance with the configuration specified in this builder.
         *
         * @return a new Get instance with the specified configuration
         */
        public Get build() {
            final Get get = new Get();
            get.url = url;
            get.queryParams = queryParams;
            get.headers = headers;

            return get;
        }
    }
}
