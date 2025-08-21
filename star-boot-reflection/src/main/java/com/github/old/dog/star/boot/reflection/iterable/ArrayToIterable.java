package com.github.old.dog.star.boot.reflection.iterable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.github.old.dog.star.boot.model.RecordEntry;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class ArrayToIterable extends ToIterable<Map.Entry<Integer, Object>> {

    @Override
    public boolean isApplicable(@Nullable Object subject) {
        return subject != null && subject.getClass().isArray();
    }

    @Override
    public Iterable<Map.Entry<Integer, Object>> makeIterable(@Nullable Object subject) {
        if (!isApplicable(subject)) {
            throw new IllegalArgumentException("You have to check `subject` on `isApplicable` method before call!");
        }

        Class<?> componentType = subject.getClass().getComponentType();

        return componentType.isPrimitive()
                ? handlePrimitiveArray(subject) : handleObjectiveArray(subject);
    }

    private Iterable<Map.Entry<Integer, Object>> handleObjectiveArray(Object subject) {
        // для объектных массивов
        final Object[] array = (Object[]) subject;
        final List<Map.Entry<Integer, Object>> iterable = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            iterable.add(new RecordEntry<>(i, array[i]));
        }
        return iterable;
    }

    private Iterable<Map.Entry<Integer, Object>> handlePrimitiveArray(Object array) {
        final int length = Array.getLength(array);
        final List<Map.Entry<Integer, Object>> result = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            Object value = Array.get(array, i);
            result.add(new RecordEntry<>(i, value));
        }

        return result;
    }

}
