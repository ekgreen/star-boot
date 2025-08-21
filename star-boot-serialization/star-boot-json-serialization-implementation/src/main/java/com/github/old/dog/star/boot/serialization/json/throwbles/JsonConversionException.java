
package com.github.old.dog.star.boot.serialization.json.throwbles;

import lombok.experimental.StandardException;

/**
 * Exception thrown to indicate an error occurred during JSON conversion processes.
 * <p>
 * This exception is typically used when an object cannot be converted
 * to or from JSON due to issues such as invalid JSON structure,
 * unsupported data types, or serialization/deserialization errors.
 * <p>
 * JsonConversionException extends RuntimeException, meaning it is unchecked
 * and does not require explicit handling or declaration in method signatures.
 * It can be utilized to propagate JSON-related runtime issues in the application.
 */
@StandardException
public class JsonConversionException extends RuntimeException {
}
