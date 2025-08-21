package com.github.old.dog.star.boot.reflection.iterable;

import java.util.Map;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import org.jetbrains.annotations.Nullable;

/**
 * A concrete implementation of the {@link ToIterable} class that provides functionality
 * to convert a {@link Map} into an {@link Iterable} of its entries. This class effectively
 * checks if an object is a {@link Map} and then creates an {@link Iterable} over the map's
 * key-value pairs in the form of {@link Map.Entry}.
 * <p>
 * Usage of this class requires a prior check using the {@code isApplicable} method to ensure
 * that the provided object can be processed as a {@link Map}. Failure to do so will result
 * in an {@link IllegalArgumentException} when attempting to create the {@link Iterable}.
 */
public class MapToIterable extends ToIterable<Map.Entry<String, Object>> {

    @Override
    public boolean isApplicable(@Nullable Object subject) {
        return ReflectionTools.isAssignable(subject, Map.class);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> makeIterable(@Nullable Object subject) {
        if (!isApplicable(subject)) {
            throw new IllegalArgumentException("You have to check `subject` on `isApplicable` method before call!");
        }

        // noinspection unchecked,DataFlowIssue,rawtypes
        return ((Map) subject).entrySet();
    }
}
