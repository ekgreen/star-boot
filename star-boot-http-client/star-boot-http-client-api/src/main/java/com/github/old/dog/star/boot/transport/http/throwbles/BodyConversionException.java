package com.github.old.dog.star.boot.transport.http.throwbles;

import lombok.experimental.StandardException;

/**
 * Exception thrown to indicate an error during the body conversion process.
 * <p>
 * This exception typically occurs when a given body cannot be converted to the
 * expected type or format. It can be used in scenarios where type conversion,
 * deserialization, or formatting operations fail.
 * <p>
 * BodyConversionException extends RuntimeException, meaning it's an unchecked
 * exception and does not require mandatory handling in the code.
 */
@StandardException
public class BodyConversionException extends RuntimeException {
}
