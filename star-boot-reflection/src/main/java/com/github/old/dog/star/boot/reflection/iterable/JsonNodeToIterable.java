package com.github.old.dog.star.boot.reflection.iterable;

import java.util.Map;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The JsonNodeToIterable class adapts an object of type JsonNode into an Iterable of key-value pairs,
 * represented as Map.Entry. It is a concrete implementation of the generic
 * ToIterable class.
 * <p>
 * This class is specifically designed to work with JsonNode objects and ensures that the given
 * object is compatible before converting it into an iterable.
 */
public class JsonNodeToIterable extends ToIterable<Map.Entry<String, Object>> {

    @Override
    public boolean isApplicable(@Nullable Object subject) {
        return ReflectionTools.isAssignable(subject, JsonNode.class);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> makeIterable(@Nullable Object subject) {
        if (!isApplicable(subject)) {
            throw new IllegalArgumentException("You have to check `subject` on `isApplicable` method before call!");
        }

        // noinspection unchecked,DataFlowIssue
        return (Iterable<Map.Entry<String, Object>>) (Iterable<?>) ((JsonNode) subject).properties();
    }
}
