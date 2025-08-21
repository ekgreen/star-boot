package com.github.old.dog.star.boot.serialization.api;

/**
 * Exception thrown when serialization or deserialization operations fail.
 * <p>
 * This is a runtime exception that wraps underlying serialization errors
 * and provides a consistent exception hierarchy for serialization operations.
 */
public class SerializationException extends RuntimeException {

    /**
     * Constructs a new serialization exception with the specified detail message.
     *
     * @param message the detail message
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new serialization exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new serialization exception with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
