package com.github.old.dog.star.boot.serialization.json.value;

import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import com.github.old.dog.star.boot.serialization.api.ReaderVisitor;

/**
 * Implementation of {@link JsonValue} that wraps an InputStream containing JSON data.
 * <p>
 * This class is used to process JSON data that is available as a stream, allowing for
 * processing of potentially large JSON data without loading it entirely into memory.
 * It delegates the actual processing to a visitor that can handle stream input.
 */
@RequiredArgsConstructor
public class InputStreamJsonValue implements JsonValue {

    /**
     * The InputStream containing JSON data to be processed.
     */
    private final InputStream is;

    /**
     * Processes the InputStream by passing it to the appropriate visitor method.
     *
     * @param <T>     the type that the visitor will return after processing
     * @param visitor the visitor that will process the InputStream
     * @return the result of the visitor's processing
     * @throws Exception if the visitor encounters an error during processing
     */
    @Override
    public <T> T read(ReaderVisitor<T> visitor) throws Exception {
        return visitor.visitInputStream(is);
    }
}
