package com.github.old.dog.star.boot.transport.http.body;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a request body containing a byte array.
 * <p>
 * This class serves as an implementation of the {@link Body} interface for cases
 * where raw binary data needs to be transmitted in an HTTP request. It encapsulates
 * a byte array that can be used as the content of the request body.
 * <p>
 * The {@code arr} field is immutable and must be provided at the time of
 * instantiation. Consumers of this class can retrieve the byte array, typically
 * for use in HTTP client libraries that require a raw byte array as input.
 */
@Getter
@RequiredArgsConstructor
public class ByteArray implements Body {
    private final byte[] arr;
}
