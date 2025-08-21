package com.github.old.dog.star.boot.serialization.api;

import java.io.InputStream;

/**
 * Visitor interface for processing serialized data from different sources.
 * <p>
 * This interface defines methods for handling serialized data in different formats:
 * String, byte array, and InputStream. Implementations should provide specific
 * deserialization logic for each format.
 *
 * @param <R> the type of object to be returned after processing
 */
public interface ReaderVisitor<R> {

    /**
     * Processes serialized data from a String source.
     *
     * @param str the string containing serialized data
     * @return the deserialized object
     * @throws Exception if processing fails
     */
    R visitString(String str) throws Exception;

    /**
     * Processes serialized data from a byte array source.
     *
     * @param bytes the byte array containing serialized data
     * @return the deserialized object
     * @throws Exception if processing fails
     */
    R visitBytes(byte[] bytes) throws Exception;

    /**
     * Processes serialized data from an InputStream source.
     *
     * @param is the InputStream containing serialized data
     * @return the deserialized object
     * @throws Exception if processing fails
     */
    R visitInputStream(InputStream is) throws Exception;
}
