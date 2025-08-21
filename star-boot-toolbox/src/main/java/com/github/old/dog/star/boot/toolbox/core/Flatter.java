package com.github.old.dog.star.boot.toolbox.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for flattening complex object structures into map representations.
 * <p>
 * This class provides methods to convert nested objects, arrays, and maps into a flat
 * structure with dot notation for nested properties. For example, a nested object with
 * property 'user.address.street' will be flattened to a map with a key 'user.address.street'.
 * <p>
 * The class supports customization of key extraction through the {@link KeyExtractor} interface.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Flatter {

    private static final String EMPTY_STRING = "";

    /**
     * Flattens a complex object structure into a map using the default {@link FieldNameKeyExtractor}.
     * <p>
     * The default extractor uses field names as keys in the resulting map.
     *
     * @param subject the object to flatten, can be null
     * @return a map containing the flattened structure with field names as keys
     */
    public static Map<String, Object> flatten(
        @Nullable Object subject
    ) {
        return Flatter.flatten(subject, new FieldNameKeyExtractor());
    }

    /**
     * Flattens a complex object structure into a map using the provided key extractor.
     * <p>
     * This method allows customization of how keys are generated in the resulting map.
     *
     * @param subject      the object to flatten, can be null
     * @param keyExtractor the strategy for extracting keys from fields
     * @return a map containing the flattened structure with custom keys
     */
    public static Map<String, Object> flatten(
        @Nullable Object subject,
        @NotNull KeyExtractor keyExtractor
    ) {
        final Map<String, Object> flattened = new LinkedHashMap<>();
        Flatter._flatten(Flatter.EMPTY_STRING, subject, keyExtractor, flattened);
        return flattened;
    }

    private static void _flatten(
        @NotNull  final String prefix,
        @Nullable final Object subject,
        @NotNull  final BiFunction<Field, Object, String> keyExtractor,
        @NotNull  final Map<String, Object> flattened
    ) {

        if (ReflectionTools.isPrimal(subject)) {
            // если null / примитивный тип / один из видов строки
            flattened.put(prefix, subject);
            return;
        }

        if (ReflectionTools.isArray(subject)) {
            ReflectionTools.forEachArray(subject, (index, value) -> {
                String name = String.format("%s[%s]", prefix, index);
                // name  = новое название с учетом индекса элемента
                // metadata = ссылка на поле массива, чтобы дочерние элементы могли получить информацию о роидтеле
                _flatten(name, value, keyExtractor, flattened);
            });
            return;
        }

        if (ReflectionTools.isTree(subject)) {
            ReflectionTools.forEachTree(subject, (key, value) -> {
                String name = String.format("%s.%s", prefix, key);
                // name  = новое название с учетом индекса элемента
                // metadata = ссылка на поле массива, чтобы дочерние элементы могли получить информацию о роидтеле
                _flatten(name, value, keyExtractor, flattened);
            });

            return;
        }

        // значит у нас сложный объект
        ReflectionTools.forEachField(subject, (field, value) -> {
            String name = prefix.isEmpty() ? keyExtractor.apply(field, value) : String.format("%s.%s", prefix, keyExtractor.apply(field, value));

            _flatten(name, value, keyExtractor, flattened);
        });

    }

    /**
     * Functional interface for extracting key names from object fields.
     * <p>
     * This interface is used by the {@link Flatter} class to determine
     * the keys in the flattened map representation of an object. It receives
     * both the field and the containing object, allowing implementations to
     * derive keys based on field metadata, annotations, or object state.
     */
    public interface KeyExtractor extends BiFunction<Field, Object, String> {
    }


    /**
     * A key extractor implementation that extracts keys from field annotations.
     * <p>
     * This extractor uses a specific annotation type and an extractor function to
     * determine keys from annotated fields.
     *
     * @param <A> the annotation type to extract from
     */
    @RequiredArgsConstructor
    public static class AnnotationKeyExtractor<A extends Annotation> implements KeyExtractor {

        private final @NotNull Class<A> annotationType;
        private final @NotNull Function<A, String> extractor;

        @Override
        public String apply(Field field, Object subject) {
            A annotation = field.getAnnotation(annotationType);
            return annotation != null ? extractor.apply(annotation) : null;
        }
    }

    /**
     * A simple key extractor implementation that uses field names as keys.
     * <p>
     * This is the default key extractor used by the {@link #flatten(Object)} method.
     */
    public static class FieldNameKeyExtractor implements KeyExtractor {
        @Override
        public String apply(Field field, Object subject) {
            return field.getName();
        }
    }

}
