package com.github.old.dog.star.boot.transport.http.call;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.old.dog.star.boot.interfaces.Converter;
import com.github.old.dog.star.boot.model.Tokens;
import com.github.old.dog.star.boot.toolbox.collections.touples.Pair;
import com.github.old.dog.star.boot.toolbox.core.Converters;
import com.github.old.dog.star.boot.transport.http.body.ByteArray;
import com.github.old.dog.star.boot.transport.http.HttpClient;
import com.github.old.dog.star.boot.transport.http.body.Body;
import com.github.old.dog.star.boot.transport.http.body.FormBody;
import com.github.old.dog.star.boot.transport.http.throwbles.BodyConversionException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A fluent API for building and executing HTTP requests.
 * <p>
 * This class provides a chainable, fluent interface for configuring and executing
 * HTTP requests using the builder pattern. It supports both GET and POST methods,
 * allows setting of headers and cookies, and provides convenient methods for handling
 * request and response data conversion.
 * <p>
 * Example usage for a GET request:
 * <pre>
 * Payload response = Call.create()
 *     .url("https://api.example.com/users")
 *     .build()
 *     .headers()
 *     .add("Accept", "application/json")
 *     .build()
 *     .get()
 *     .execute(httpClient);
 * </pre>
 * <p>
 * Example usage for a POST request with JSON body:
 * <pre>
 * User user = Call.create()
 *     .url("https://api.example.com/users")
 *     .build()
 *     .headers()
 *     .applicationJson()
 *     .build()
 *     .post(userRequest)
 *     .execute(httpClient, User.class);
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Call {

    /**
     * Query parameters to be appended to the URL.
     */
    private final Map<String, Object> queryParams = new HashMap<>();

    /**
     * HTTP headers to be included with the request.
     */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * The target URL for the HTTP request.
     */
    private String url;

    /**
     * Creates a new Call instance to start building an HTTP request.
     *
     * @return a new Call instance
     */
    public static Call create() {
        return new Call();
    }

    /**
     * Sets the URL for this HTTP request and returns a Query builder for
     * adding query parameters.
     *
     * @param url the target URL for the HTTP request
     * @return a Query builder for adding query parameters
     */
    public Query url(String url) {
        return new Query(url, this);
    }

    /**
     * Internal method to set the URL for this HTTP request.
     *
     * @param url the target URL for the HTTP request
     */
    private void withUrl(String url) {
        this.url = url;
    }

    /**
     * Internal method to add query parameters to this HTTP request.
     *
     * @param queryParams the query parameters to add
     */
    private void withQueryParams(Map<String, Object> queryParams) {
        this.queryParams.putAll(queryParams);
    }

    /**
     * Returns a Header builder for adding HTTP headers to this request.
     *
     * @return a Header builder for adding HTTP headers
     */
    public Header headers() {
        return new Header(this);
    }

    /**
     * Internal method to add HTTP headers to this HTTP request.
     *
     * @param headers the HTTP headers to add
     */
    private void withHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * Returns a Cookies builder for adding cookies to this request.
     *
     * @return a Cookies builder for adding cookies
     */
    public Cookies cookies() {
        return new Cookies(this);
    }

    /**
     * Internal method to add cookies to this HTTP request.
     * Cookies are added as a single Cookie header with values separated by semicolons.
     *
     * @param cookies the list of cookie name-value pairs to add
     */
    private void withCookies(List<Pair<String, String>> cookies) {
        final String header = cookies.stream()
            .map(cookie -> cookie.concat("="))
            .collect(Collectors.joining(";"));

        if (ObjectUtils.isEmpty(header)) {
            return;
        }

        String cookie = this.headers.get(Tokens.COOKIE);

        if (!ObjectUtils.isEmpty(cookie)) {
            cookie = cookie + ";" + header;
        } else {
            cookie = header;
        }

        this.headers.put(Tokens.COOKIE, cookie);
    }

    /**
     * Creates a GetCaller for executing an HTTP GET request.
     *
     * @return a GetCaller for executing the request
     */
    public GetCaller get() {
        return new GetCaller();
    }

    /**
     * Executes an HTTP GET request using the provided HTTP client.
     * This is a convenience method that combines creation of a GetCaller and execution.
     *
     * @param client the HTTP client to use for execution
     * @return the HTTP response payload
     */
    public Payload get(HttpClient client) {
        GetCaller caller = new GetCaller();
        return caller.execute(client);
    }

    /**
     * Creates a PostCaller for executing an HTTP POST request with the specified body.
     *
     * @param body the request body to send
     * @return a PostCaller for executing the request
     */
    public PostCaller post(Body body) {
        return new PostCaller(body);
    }

    /**
     * Creates a PostCaller for executing an HTTP POST request with the specified text.
     * The text is converted to bytes using UTF-8 encoding.
     *
     * @param text the text to send as the request body
     * @return a PostCaller for executing the request
     */
    public PostCaller post(String text) {
        return post(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a PostCaller for executing an HTTP POST request with the specified object.
     * The object is converted to an appropriate body format based on the Content-Type header.
     * Supported content types include application/json and application/x-www-form-urlencoded.
     *
     * @param payload the object to send as the request body
     * @return a PostCaller for executing the request
     * @throws BodyConversionException if the Content-Type header is missing or unsupported
     */
    public PostCaller post(Object payload) {
        final String contentTypeHeader = headers.get(Tokens.CONTENT_TYPE);

        if (contentTypeHeader == null) {
            throw new BodyConversionException("for raw object types header Content-Type must be defined");
        }

        final String contentType = contentTypeHeader.split(";")[0];

        Converter<Object, Body> converter = switch (contentType) {
            case Tokens.MEDIA_TYPE_APPLICATION_JSON -> Converters.jsonWriteAsBytes().after(ByteArray::new);
            case Tokens.MEDIA_TYPE_X_FORM -> Call.formDataConverter();
            case null, default -> throw new BodyConversionException("for raw object types header Content-Type must be defined");
        };

        return post(converter.convert(payload));
    }

    /**
     * Internal method to convert a response payload to a specific type based on the Content-Type header.
     *
     * @param <R>          the target type of the conversion
     * @param payload      the HTTP response payload to convert
     * @param responseType the class of the target type
     * @return the converted object of type R
     * @throws BodyConversionException if the Content-Type header is missing or unsupported
     */
    private <R> R castResponse(Payload payload, Class<R> responseType) {
        Converter<byte[], R> converter = switch (payload.header(Tokens.CONTENT_TYPE)) {
            case Tokens.MEDIA_TYPE_APPLICATION_JSON -> Converters.jsonReaderAs(responseType);
            case null, default -> throw new BodyConversionException("for raw object types header Content-Type must be defined");
        };

        return payload.convert(converter);
    }

    /**
     * Inner class for executing HTTP GET requests.
     * <p>
     * This class provides methods for executing the configured GET request
     * and optionally converting the response to a specific type.
     */
    public class GetCaller {

        /**
         * Executes the GET request using the provided HTTP client and returns the raw response.
         *
         * @param httpClient the HTTP client to use for execution
         * @return the HTTP response payload
         */
        public Payload execute(HttpClient httpClient) {
            Get get = Get.builder(url)
                .withQueryParams(queryParams)
                .withHeaders(headers)
                .build();

            return httpClient.get(get);
        }

        /**
         * Executes the GET request using the provided HTTP client and converts the response
         * to the specified type.
         *
         * @param <T>          the target type of the conversion
         * @param httpClient   the HTTP client to use for execution
         * @param responseType the class of the target type
         * @return the converted response of type T
         * @throws BodyConversionException if the response cannot be converted to the target type
         */
        public <T> T execute(HttpClient httpClient, Class<T> responseType) {
            Get get = Get.builder(url)
                .withQueryParams(queryParams)
                .withHeaders(headers)
                .build();

            return castResponse(httpClient.get(get), responseType);
        }
    }

    /**
     * Inner class for executing HTTP POST requests.
     * <p>
     * This class provides methods for executing the configured POST request
     * with a specified body and optionally converting the response to a specific type.
     */
    @RequiredArgsConstructor
    public class PostCaller {
        /**
         * The request body to send with the POST request.
         */
        private final Body payload;

        /**
         * Executes the POST request using the provided HTTP client and returns the raw response.
         *
         * @param httpClient the HTTP client to use for execution
         * @return the HTTP response payload
         */
        public Payload execute(HttpClient httpClient) {
            Post post = Post.builder(url)
                .withQueryParams(queryParams)
                .withHeaders(headers)
                .withPayload(payload)
                .build();

            return httpClient.post(post);
        }

        /**
         * Executes the POST request using the provided HTTP client and converts the response
         * to the specified type.
         *
         * @param <T>          the target type of the conversion
         * @param httpClient   the HTTP client to use for execution
         * @param responseType the class of the target type
         * @return the converted response of type T
         * @throws BodyConversionException if the response cannot be converted to the target type
         */
        public <T> T execute(HttpClient httpClient, Class<T> responseType) {
            Post post = Post.builder(url)
                .withQueryParams(queryParams)
                .withHeaders(headers)
                .withPayload(payload)
                .build();

            Payload response = httpClient.post(post);

            return castResponse(response, responseType);
        }

        /**
         * Executes the POST request using the provided HTTP client without expecting a response.
         * This is useful for fire-and-forget operations where the response is not needed.
         *
         * @param httpClient the HTTP client to use for execution
         */
        public void apply(HttpClient httpClient) {
            Post post = Post.builder(url)
                .withQueryParams(queryParams)
                .withHeaders(headers)
                .withPayload(payload)
                .build();

            httpClient.post(post);
        }
    }

    /**
     * Inner class for building query parameters for an HTTP request.
     * <p>
     * This class provides methods for adding query parameters to the request URL.
     */
    @RequiredArgsConstructor
    public static class Query {
        /**
         * Query parameters to be added to the request URL.
         */
        private final Map<String, Object> queryParams = new HashMap<>();

        /**
         * The target URL for the HTTP request.
         */
        private final String url;

        /**
         * Reference to the parent Call instance.
         */
        private final Call root;

        /**
         * Adds a query parameter to the request URL.
         *
         * @param key the name of the query parameter
         * @param val the value of the query parameter
         * @return this Query instance for method chaining
         */
        public Query add(String key, Object val) {
            queryParams.put(key, val);
            return this;
        }

        /**
         * Completes the URL configuration and returns to the parent Call instance.
         *
         * @return the parent Call instance
         */
        public Call build() {
            root.withUrl(url);
            root.withQueryParams(queryParams);

            return root;
        }
    }

    /**
     * Inner class for building HTTP headers for a request.
     * <p>
     * This class provides methods for adding headers to the HTTP request,
     * including convenience methods for common headers such as Content-Type.
     */
    @RequiredArgsConstructor
    public static class Header {
        /**
         * HTTP headers to be added to the request.
         */
        private final Map<String, String> headers = new HashMap<>();

        /**
         * Reference to the parent Call instance.
         */
        private final Call root;

        /**
         * Sets the Content-Type header to application/json.
         *
         * @return this Header instance for method chaining
         */
        public Header applicationJson() {
            add(Tokens.CONTENT_TYPE, Tokens.MEDIA_TYPE_APPLICATION_JSON);
            return this;
        }

        /**
         * Sets the Content-Type header to application/x-www-form-urlencoded with UTF-8 charset.
         *
         * @return this Header instance for method chaining
         */
        public Header formUrlencoded() {
            add(Tokens.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
            return this;
        }

        /**
         * Sets the Host header.
         *
         * @param host the host value
         * @return this Header instance for method chaining
         */
        public Header host(String host) {
            add("Host", host);
            return this;
        }

        /**
         * Sets the Origin header.
         *
         * @param host the origin value
         * @return this Header instance for method chaining
         */
        public Header origin(String host) {
            add("Origin", host);
            return this;
        }

        /**
         * Sets the Refer header (possibly a typo of Referer).
         *
         * @param host the referer value
         * @return this Header instance for method chaining
         */
        public Header refet(String host) {
            add("Refer", host);
            return this;
        }

        /**
         * Sets the Cookie header with the provided cookie string.
         *
         * @param cookies the cookie string
         * @return this Header instance for method chaining
         */
        public Header cookies(String cookies) {
            if (!ObjectUtils.isEmpty(cookies)) {
                add(Tokens.COOKIE, cookies);
            }

            return this;
        }

        /**
         * Sets the Cache-Control header to no-cache.
         *
         * @return this Header instance for method chaining
         */
        public Header cacheControl() {
            add(Tokens.CACHE_CONTROL, "no-cache");
            return this;
        }

        /**
         * Adds a header without a value.
         *
         * @param name the header name
         * @return this Header instance for method chaining
         */
        public Header add(String name) {
            this.headers.put(name, null);
            return this;
        }

        /**
         * Adds a header with a value.
         *
         * @param name  the header name
         * @param value the header value
         * @return this Header instance for method chaining
         */
        public Header add(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        /**
         * Adds multiple headers with values.
         * <p>
         * This method accepts a variable number of string arguments that should be provided
         * in pairs, where each pair consists of a header name followed by its value.
         *
         * @param name    the first header name
         * @param value   the first header value
         * @param headers additional header name-value pairs
         * @return this Header instance for method chaining
         * @throws IllegalArgumentException if the number of additional headers is not even
         */
        public Header add(String name, String value, String... headers) {
            add(name, value);

            if (headers != null && headers.length % 2 != 0) {
                throw new IllegalArgumentException("'headers' argument should contains even number of variables");
            }

            if (headers != null) {
                for (int i = 0; i < headers.length; i = i + 2) {
                    add(headers[i], headers[i + 1]);
                }
            }

            return this;
        }

        /**
         * Completes the header configuration and returns to the parent Call instance.
         *
         * @return the parent Call instance
         */
        public Call build() {
            root.withHeaders(headers);

            return root;
        }
    }

    /**
     * Inner class for building cookies for an HTTP request.
     * <p>
     * This class provides methods for adding cookies to the HTTP request.
     * Cookies are ultimately added as a Cookie header in the request.
     */
    @RequiredArgsConstructor
    public static class Cookies {
        /**
         * List of cookie name-value pairs to be added to the request.
         */
        private final List<Pair<String, String>> values = new ArrayList<>();

        /**
         * Reference to the parent Call instance.
         */
        private final Call root;

        /**
         * Adds a cookie to the request.
         *
         * @param name  the cookie name
         * @param value the cookie value
         * @return this Cookies instance for method chaining
         */
        public Cookies add(String name, String value) {
            if (!ObjectUtils.isEmpty(name) && !ObjectUtils.isEmpty(value)) {
                this.values.add(Pair.keyValue(name, value));
            }

            return this;
        }

        /**
         * Completes the cookie configuration and returns to the parent Call instance.
         *
         * @return the parent Call instance
         */
        public Call build() {
            root.withCookies(values);
            return root;
        }
    }

    /**
     * Creates a converter that transforms objects to form data bodies using
     * field annotations. Each field with a {@link JsonProperty} annotation will be
     * included in the form data with the annotation value as the key.
     *
     * @return a converter from Object to Body for form data submission
     */
    public static Converter<Object, Body> formDataConverter() {
        return object -> {
            final FormBody body = new FormBody();

            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                String key = field.getAnnotation(JsonProperty.class).value();
                String value;

                try {
                    value = Optional.ofNullable(field.get(object))
                        .map(String::valueOf)
                        .orElse(null);
                } catch (IllegalArgumentException | IllegalAccessException illegalAction) {
                    value = null;
                }

                body.add(key, value);
            }

            return body;
        };
    }
}
