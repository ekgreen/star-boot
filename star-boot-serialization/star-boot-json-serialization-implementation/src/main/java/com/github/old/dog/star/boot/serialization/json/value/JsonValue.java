package com.github.old.dog.star.boot.serialization.json.value;

import com.github.old.dog.star.boot.serialization.api.ReaderVisitor;

/**
 * Interface representing a value that contains JSON data in some form.
 * <p>
 * This interface is part of a visitor pattern implementation for processing JSON data
 * from different sources (String, byte array, InputStream) in a uniform way.
 * Implementations of this interface wrap specific JSON data sources and delegate
 * the processing to an appropriate visitor method.
 */
public interface JsonValue {
    /**
     * Processes the JSON data contained in this value using the provided visitor.
     * <p>
     * This method delegates to the appropriate visitor method based on the concrete
     * implementation of this interface.
     *
     * @param <T>     the type that the visitor will return after processing
     * @param visitor the visitor that will process the JSON data
     * @return the result of the visitor's processing
     * @throws Exception if the visitor encounters an error during processing
     */
    <T> T read(ReaderVisitor<T> visitor) throws Exception;
}
