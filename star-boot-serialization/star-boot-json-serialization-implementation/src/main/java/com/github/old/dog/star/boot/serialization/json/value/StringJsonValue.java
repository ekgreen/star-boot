package com.github.old.dog.star.boot.serialization.json.value;

import lombok.RequiredArgsConstructor;
import com.github.old.dog.star.boot.serialization.api.ReaderVisitor;

/**
 * Implementation of {@link JsonValue} that wraps a String containing JSON data.
 * <p>
 * This class is used to process JSON data that is already available as a String.
 * It delegates the actual processing to a visitor that can handle String input.
 */
@RequiredArgsConstructor
public class StringJsonValue implements JsonValue {

    /**
     * The String containing JSON data to be processed.
     */
    private final String value;

    /**
     * Processes the String by passing it to the appropriate visitor method.
     *
     * @param <T> the type that the visitor will return after processing
     * @param visitor the visitor that will process the String
     * @return the result of the visitor's processing
     * @throws Exception if the visitor encounters an error during processing
     */
    @Override
    public <T> T read(ReaderVisitor<T> visitor) throws Exception {
        return visitor.visitString(value);
    }
}
