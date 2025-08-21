package com.github.old.dog.star.boot.reflection.iterable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.github.old.dog.star.boot.model.RecordEntry;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import org.jetbrains.annotations.Nullable;

/**
 * A class that extends {@link ToIterable} and provides a way to convert
 * objects implementing the {@link Iterable} interface into an {@link Iterable}
 * of {@link Map.Entry} objects. Each entry contains an index as the key and
 * the corresponding element from the original {@link Iterable} as the value.
 * <p>
 * This class ensures that the input is properly checked for compatibility using
 * the {@link #isApplicable(Object)} method before attempting the conversion.
 */
public class IterableToIterable extends ToIterable<Map.Entry<Integer, Object>> {

    @Override
    public boolean isApplicable(@Nullable Object subject) {
        return ReflectionTools.isAssignable(subject, Iterable.class);
    }

    @Override
    public Iterable<Map.Entry<Integer, Object>> makeIterable(@Nullable Object subject) {
        if (!isApplicable(subject)) {
            throw new IllegalArgumentException("You have to check `subject` on `isApplicable` method before call!");
        }

        final List<Map.Entry<Integer, Object>> iterable = new ArrayList<>();

        int i = 0;
        // noinspection DataFlowIssue, unchecked
        for (Object object : (Iterable<Object>) subject) {
            iterable.add(new RecordEntry<>(i++, object));
        }

        return iterable;
    }


}
