package com.github.old.dog.star.boot.model;

import lombok.experimental.UtilityClass;

/**
 * A utility class that provides constants for commonly used tokens, such as
 * HTTP header names and media types. These constants can be referenced
 * across the application to maintain consistency and reduce the risk of
 * errors caused by hardcoding string values.
 */
@UtilityClass
public class Tokens {

    /**
     * A constant value representing an unknown state or undefined value.
     * This may be used as a placeholder, default value, or marker
     * to indicate the absence of specific information.
     */
    public static final String UNKNOWN = "unknown";

    /**
     * A constant string representing the "Cookie" HTTP header field.
     * Typically used to store and send small pieces of data to the server
     * as part of an HTTP request, often for session identification or
     * tracking purposes.
     */
    public static final String COOKIE = "Cookie";

    /**
     * A constant string representing the "Content-Type" HTTP header field.
     * This header is used to indicate the media type of the resource being sent
     * in an HTTP request or response. It informs the recipient of the
     * format of the data being transmitted, such as "application/json" or "text/html".
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * A constant string representing the "Cache-Control" HTTP header field.
     * This header is used to specify directives for caching mechanisms in
     * both requests and responses. It defines rules for cache behavior,
     * such as expiration time, revalidation, or prevention of caching.
     */
    public static final String CACHE_CONTROL = "Cache-Control";

    /**
     * A constant string representing the media type "application/json".
     * This media type is commonly used to specify that the content being sent
     * or received in an HTTP request or response is formatted as JSON (JavaScript Object Notation).
     * JSON is a lightweight data-interchange format widely used for web services
     * and APIs, enabling structured and efficient data exchange between systems.
     */
    public static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";

    /**
     * A constant string representing the media type "application/x-www-form-urlencoded".
     * This media type is commonly used in HTTP requests, particularly in
     * form submissions where data is encoded as key-value pairs. It is often used
     * with the HTTP POST method to send form data to the server.
     */
    public static final String MEDIA_TYPE_X_FORM = "application/x-www-form-urlencoded";



}
