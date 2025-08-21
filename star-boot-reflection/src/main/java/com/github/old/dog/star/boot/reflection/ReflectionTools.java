package com.github.old.dog.star.boot.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import com.github.old.dog.star.boot.interfaces.BiExConsumer;
import com.github.old.dog.star.boot.interfaces.BiExFunction;
import com.github.old.dog.star.boot.reflection.iterable.ArrayToIterable;
import com.github.old.dog.star.boot.reflection.iterable.IterableToIterable;
import com.github.old.dog.star.boot.reflection.iterable.JsonNodeToIterable;
import com.github.old.dog.star.boot.reflection.iterable.MapToIterable;
import com.github.old.dog.star.boot.reflection.iterable.ToIterable;
import com.github.old.dog.star.boot.throwbles.Throwables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Utility class providing comprehensive reflection operations for Java objects.
 * <p>
 * This class offers a wide range of reflection-based utilities including:
 * <ul>
 *   <li>Type checking and assignability verification</li>
 *   <li>Field access and manipulation with proper accessibility handling</li>
 *   <li>Generic type extraction from inheritance hierarchies</li>
 *   <li>Object iteration capabilities for various data structures</li>
 *   <li>Instance creation and constructor invocation</li>
 * </ul>
 * <p>
 * The class categorizes objects into different types such as primitive types, iterables,
 * trees (Map-like structures), arrays, and time-related objects, providing specialized
 * handling for each category.
 * <p>
 * All methods are static and the class cannot be instantiated.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionTools {

    /**
     * Converter for transforming array-like objects into iterable structures.
     * Handles both actual arrays and Iterable implementations.
     */
    private static final ToIterable<Entry<Integer, Object>> ITERABLE_ARRAY = new ArrayToIterable()
        .ifElse(new IterableToIterable());

    /**
     * Converter for transforming tree-like objects into iterable structures.
     * Handles both Map implementations and Jackson JsonNode objects.
     */
    private static final ToIterable<Map.Entry<String, Object>> ITERABLE_TREE = new MapToIterable()
        .ifElse(new JsonNodeToIterable());

    /**
     * Checks if an object is assignable to a specific class type.
     *
     * @param subject the object to check for assignability
     * @param assignableFrom the target class to check assignability to
     * @return true if the object is not null and its class is assignable to the target class
     */
    public static boolean isAssignable(@Nullable Object subject, Class<?> assignableFrom) {
        return subject != null && ReflectionTools.isAssignable(subject.getClass(), assignableFrom);
    }

    /**
     * Checks if a class is assignable to another class type.
     *
     * @param cls the class to check for assignability
     * @param assignableFrom the target class to check assignability to
     * @return true if the class is assignable to the target class
     */
    public static boolean isAssignable(@NotNull Class<?> cls, Class<?> assignableFrom) {
        return assignableFrom.isAssignableFrom(cls);
    }

    /**
     * Checks if a class is not assignable to another class type.
     *
     * @param cls the class to check for non-assignability
     * @param assignableFrom the target class to check non-assignability to
     * @return true if the class is not assignable to the target class
     */
    public static boolean notAssignable(@NotNull Class<?> cls, Class<?> assignableFrom) {
        return !ReflectionTools.isAssignable(cls, assignableFrom);
    }

    /**
     * Determines if a class represents a primitive or primitive wrapper type.
     * <p>
     * This method considers the following as primitive types:
     * <ul>
     *   <li>Java primitive types (int, long, double, etc.)</li>
     *   <li>Primitive wrapper classes (Integer, Long, Double, etc.)</li>
     *   <li>CharSequence implementations (String, StringBuilder, etc.)</li>
     * </ul>
     *
     * @param cls the class to check
     * @return true if the class represents a primitive type
     */
    public static boolean isPrimal(@NotNull Class<?> cls) {
        return cls.isPrimitive()
               || ReflectionTools.isAssignable(cls, CharSequence.class)
               || cls == Integer.class
               || cls == Long.class
               || cls == Double.class
               || cls == Float.class
               || cls == Short.class
               || cls == Byte.class
               || cls == Boolean.class
               || cls == Character.class;

    }

    /**
     * Determines if an object represents a primitive or primitive wrapper type.
     *
     * @param subject the object to check
     * @return true if the object is not null and its class represents a primitive type
     */
    public static boolean isPrimal(@Nullable Object subject) {
        return subject != null && ReflectionTools.isPrimal(subject.getClass());
    }

    /**
     * Determines if a class does not represent a primitive type.
     *
     * @param cls the class to check
     * @return true if the class does not represent a primitive type
     */
    public static boolean notPrimal(@NotNull Class<?> cls) {
        return !ReflectionTools.isPrimal(cls);
    }

    /**
     * Determines if an object does not represent a primitive type.
     *
     * @param subject the object to check
     * @return true if the object is null or its class does not represent a primitive type
     */
    public static boolean notPrimal(@Nullable Object subject) {
        return !ReflectionTools.isPrimal(subject);
    }

    /**
     * Determines if an object can be iterated over.
     * <p>
     * An object is considered iterable if it's either an array-like structure
     * or a tree-like structure (Map or JsonNode).
     *
     * @param subject the object to check
     * @return true if the object can be iterated over
     */
    public static boolean isIterable(@Nullable Object subject) {
        if (subject == null) {
            return false;
        }

        return ReflectionTools.isArray(subject) || ReflectionTools.isTree(subject);
    }

    /**
     * Determines if an object cannot be iterated over.
     *
     * @param subject the object to check
     * @return true if the object cannot be iterated over
     */
    public static boolean notIterable(@Nullable Object subject) {
        return !isIterable(subject);
    }

    /**
     * Determines if an object represents a tree-like structure.
     * <p>
     * Tree-like structures include:
     * <ul>
     *   <li>Map implementations</li>
     *   <li>Jackson JsonNode objects</li>
     * </ul>
     *
     * @param subject the object to check
     * @return true if the object represents a tree-like structure
     */
    public static boolean isTree(@Nullable Object subject) {
        if (subject == null) {
            return false;
        }

        return isAssignable(subject, Map.class)
               || isAssignable(subject, JsonNode.class);
    }

    /**
     * Determines if an object does not represent a tree-like structure.
     *
     * @param subject the object to check
     * @return true if the object does not represent a tree-like structure
     */
    public static boolean notTree(@Nullable Object subject) {
        return !isTree(subject);
    }

    /**
     * Determines if an object represents an array-like structure.
     * <p>
     * Array-like structures include:
     * <ul>
     *   <li>Java arrays</li>
     *   <li>Iterable implementations (Collections, Lists, Sets, etc.)</li>
     * </ul>
     *
     * @param subject the object to check
     * @return true if the object represents an array-like structure
     */
    public static boolean isArray(@Nullable Object subject) {
        if (subject == null) {
            return false;
        }

        Class<?> subjectCls = subject.getClass();

        return subjectCls.isArray()
               || isAssignable(subject, Iterable.class);
    }

    /**
     * Determines if an object does not represent an array-like structure.
     *
     * @param subject the object to check
     * @return true if the object does not represent an array-like structure
     */
    public static boolean notArray(@Nullable Object subject) {
        return !isArray(subject);
    }

    /**
     * Determines if a class represents a time-related type.
     * <p>
     * Time-related types include:
     * <ul>
     *   <li>Java 8+ temporal types (LocalDate, LocalDateTime, etc.)</li>
     *   <li>Legacy Date classes</li>
     * </ul>
     *
     * @param cls the class to check
     * @return true if the class represents a time-related type
     */
    public static boolean isTime(@NotNull Class<?> cls) {
        // noinspection PointlessBooleanExpression
        return false
               || ReflectionTools.isAssignable(cls, Temporal.class)
               || ReflectionTools.isAssignable(cls, Date.class);
    }

    /**
     * Determines if an object represents a time-related type.
     *
     * @param subject the object to check
     * @return true if the object is not null and its class represents a time-related type
     */
    public static boolean isTime(@Nullable Object subject) {
        return subject != null && ReflectionTools.isTime(subject.getClass());
    }

    /**
     * Determines if a class does not represent a time-related type.
     *
     * @param cls the class to check
     * @return true if the class does not represent a time-related type
     */
    public static boolean notTime(@NotNull Class<?> cls) {
        return !isTime(cls);
    }

    /**
     * Determines if an object does not represent a time-related type.
     *
     * @param subject the object to check
     * @return true if the object is null or its class does not represent a time-related type
     */
    public static boolean notTime(@Nullable Object subject) {
        return !isTime(subject);
    }

    /**
     * Converts a tree-like object into an iterable of key-value pairs.
     * <p>
     * This method handles Map objects and Jackson JsonNode objects, converting them
     * into an iterable structure where keys are strings and values are objects.
     *
     * @param subject the tree-like object to convert
     * @return an iterable of string-object entries, or empty list if conversion is not possible
     */
    public static @NotNull Iterable<Map.Entry<String, Object>> treeToIterable(@Nullable Object subject) {
        final ToIterable<Map.Entry<String, Object>> converter = ReflectionTools.ITERABLE_TREE;

        if (ReflectionTools.notTree(subject) || !converter.isApplicable(subject)) {
            return List.of();
        }

        return converter.makeIterable(subject);
    }

    /**
     * Iterates over a tree-like object, applying a consumer function to each key-value pair.
     *
     * @param subject the tree-like object to iterate over
     * @param consumer the function to apply to each key-value pair
     */
    public static void forEachTree(@Nullable Object subject, BiConsumer<String, Object> consumer) {
        ReflectionTools
            .treeToIterable(subject)
            .forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    /**
     * Converts an array-like object into an iterable of index-value pairs.
     * <p>
     * This method handles Java arrays and Iterable implementations, converting them
     * into an iterable structure where keys are integer indices and values are objects.
     *
     * @param subject the array-like object to convert
     * @return an iterable of integer-object entries, or empty list if conversion is not possible
     */
    public static @NotNull Iterable<Map.Entry<Integer, Object>> arrayToIterable(@Nullable Object subject) {
        final ToIterable<Map.Entry<Integer, Object>> converter = ReflectionTools.ITERABLE_ARRAY;

        if (ReflectionTools.notArray(subject) || !converter.isApplicable(subject)) {
            return List.of();
        }

        return converter.makeIterable(subject);
    }

    /**
     * Iterates over an array-like object, applying a consumer function to each index-value pair.
     *
     * @param subject the array-like object to iterate over
     * @param consumer the function to apply to each index-value pair
     */
    public static void forEachArray(@Nullable Object subject, BiConsumer<Integer, Object> consumer) {
        ReflectionTools
            .arrayToIterable(subject)
            .forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    /**
     * Iterates over any iterable object (both tree-like and array-like structures).
     * <p>
     * This method combines tree and array iteration, applying the consumer to all
     * key-value pairs from both structures if applicable.
     *
     * @param subject the iterable object to iterate over
     * @param consumer the function to apply to each key-value pair
     */
    public static void forEachIterable(@Nullable Object subject, BiConsumer<Object, Object> consumer) {
        if (ReflectionTools.notIterable(subject)) {
            return;
        }

        ReflectionTools
            .treeToIterable(subject)
            .forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
        ReflectionTools
            .arrayToIterable(subject)
            .forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    /**
     * Iterates over all declared fields of an object, applying a consumer function to each field and its value.
     * <p>
     * This method uses reflection to access all declared fields of the object's class,
     * including private fields, and provides both the Field object and its current value
     * to the consumer function.
     *
     * @param subject the object whose fields to iterate over
     * @param consumer the function to apply to each field-value pair
     */
    public static void forEachField(@Nullable Object subject, BiConsumer<Field, Object> consumer) {
        if (Objects.isNull(subject)) {
            return;
        }

        for (Field subjectField : subject.getClass().getDeclaredFields()) {
            ReflectionTools.accessValue(subjectField, subject, BiExConsumer.beExceptional(consumer));
        }
    }

    /**
     * Sets the value of a field on an object, handling accessibility as needed.
     * <p>
     * This method temporarily makes the field accessible if necessary, sets the value,
     * and then restores the original accessibility state.
     *
     * @param subjectField the field to set the value on
     * @param subject the object instance containing the field
     * @param value the new value to set
     * @throws RuntimeException if the field cannot be accessed or set
     */
    public static void setValue(@NotNull Field subjectField, @Nullable Object subject, @Nullable Object value) {
        ReflectionTools.accessAccessible(subjectField, subject, (field, object) -> field.set(object, value));
    }

    /**
     * Accesses the value of a field and provides both the field and its value to a consumer.
     * <p>
     * This method reads the current value of the field and passes both the Field object
     * and the retrieved value to the provided consumer function.
     *
     * @param field the field to access
     * @param subject the object instance containing the field
     * @param consumer the function to receive the field and its value
     */
    public static void accessValue(@NotNull Field field, @Nullable Object subject, @NotNull BiExConsumer<Field, Object> consumer) {
        Object value = ReflectionTools.readValue(field, subject);
        consumer.accept(field, value);
    }

    /**
     * Reads the value of a field from an object, handling accessibility as needed.
     * <p>
     * This method temporarily makes the field accessible if necessary, reads the value,
     * and then restores the original accessibility state.
     *
     * @param subjectField the field to read the value from
     * @param subject the object instance containing the field
     * @return the current value of the field
     * @throws RuntimeException if the field cannot be accessed
     */
    public static Object readValue(@NotNull Field subjectField, @Nullable Object subject) {
        return ReflectionTools.applyAccessible(subjectField, subject, Field::get);
    }

    /**
     * Applies a function to a field with proper accessibility handling.
     * <p>
     * This method temporarily makes the field accessible if it's not already accessible,
     * applies the provided function, and then restores the original accessibility state.
     * This is the core method used by other field access operations.
     *
     * @param <R> the return type of the function
     * @param field the field to access
     * @param subject the object instance containing the field
     * @param function the function to apply to the field and object
     * @return the result of applying the function, or null if the subject is null
     * @throws RuntimeException if the field operation fails
     */
    public static <R> R applyAccessible(@NotNull Field field, @Nullable Object subject, @NotNull BiExFunction<Field, Object, R> function) {
        if (subject == null) {
            return null;
        }

        boolean wasAccessible = field.canAccess(subject);

        try {
            if (!wasAccessible) {
                field.setAccessible(true);
            }

            return function.apply(field, subject);
        } finally {
            if (!wasAccessible) {
                field.setAccessible(false);
            }
        }
    }

    /**
     * Executes a consumer function on a field with proper accessibility handling.
     * <p>
     * This is a convenience method that wraps a BiExConsumer as a BiExFunction
     * and delegates to {@link #applyAccessible}.
     *
     * @param field the field to access
     * @param subject the object instance containing the field
     * @param consumer the consumer to execute on the field and object
     */
    public static void accessAccessible(@NotNull Field field, @Nullable Object subject, @NotNull BiExConsumer<Field, Object> consumer) {
        ReflectionTools.applyAccessible(field, subject, consumer.asFunction());
    }

    /**
     * Creates a new instance of a class using its default no-arguments constructor.
     * <p>
     * This method uses reflection to invoke the default constructor and handles
     * any exceptions that may occur during instantiation.
     *
     * @param <T> the type of the class to instantiate
     * @param cls the class to create an instance of
     * @return a new instance of the class, or null if instantiation fails or cls is null
     * @throws RuntimeException if instantiation fails (wrapped from the original exception)
     */
    public static <T> T newInstance(@Nullable Class<T> cls) {
        T t = null;

        if (Objects.isNull(cls)) {
            return t;
        }

        try {
            t = cls.getDeclaredConstructor().newInstance();
        } catch (Throwable anyException) {
            Throwables.sneakyThrow(anyException);
        }

        return t;
    }

    /**
     * Creates a new instance of a class using a constructor with specific parameter types.
     * <p>
     * Note: The current implementation has a bug - it doesn't use the provided constructor
     * parameters and arguments, always calling the no-args constructor instead.
     *
     * @param <T> the type of the class to instantiate
     * @param cls the class to create an instance of
     * @param constructorParameterTypes the parameter types of the desired constructor
     * @param constructorArguments the arguments to pass to the constructor
     * @return a new instance of the class, or null if instantiation fails
     * @throws RuntimeException if instantiation fails (wrapped from the original exception)
     */
    public static <T> T newInstance(
        @NotNull Class<T> cls,
        @NotNull Class<?>[] constructorParameterTypes,
        @NotNull Object[] constructorArguments
    ) {
        T t = null;

        if (Objects.isNull(cls) || constructorParameterTypes.length != constructorArguments.length) {
            return t;
        }

        try {
            t = cls.getDeclaredConstructor().newInstance();
        } catch (Throwable anyException) {
            Throwables.sneakyThrow(anyException);
        }

        return t;
    }

    /**
     * Extracts generic type arguments from a class hierarchy.
     * <p>
     * This method searches through the inheritance hierarchy of a child class to find
     * where it implements or extends the expected super class with generic type parameters,
     * then extracts and returns those generic type arguments.
     * <p>
     * The method uses breadth-first search through the class hierarchy to locate
     * parameterized type implementations.
     *
     * @param child the child class to analyze
     * @param expectedSuperClass the parent class or interface with generic types to find
     * @return an array of generic type classes, or empty array if not found or not assignable
     * @throws IllegalStateException if the class is assignable but generic types cannot be extracted
     */
    public static Class<?>[] getParentGenericTypes(@NotNull Class<?> child, @NotNull Class<?> expectedSuperClass) {
        if (ReflectionTools.notAssignable(child, expectedSuperClass)) {
            return new Class[0];
        }

        final Function<ParameterizedType, Class<?>[]> extractor = paramType -> {
            Type[] typeArgs = paramType.getActualTypeArguments();
            return Arrays.stream(typeArgs)
                .map(arg -> arg instanceof Class<?> cls ? cls : null)
                .toArray(Class[]::new);
        };

        final Queue<Class<?>> queue = new ArrayDeque<>();
        queue.add(child);

        while (!queue.isEmpty()) {
            Class<?> polled = queue.poll();

            for (Type genericType : polled.getGenericInterfaces()) {
                if (genericType instanceof Class<?> rawClass) {
                    queue.add(rawClass);
                    continue;
                }

                if (false
                    || !(genericType instanceof ParameterizedType paramType)
                    || !(paramType.getRawType() instanceof Class<?> rawClass)
                ) {
                    continue;
                }

                if (rawClass.equals(expectedSuperClass)) {
                    return extractor.apply(paramType);
                }

                queue.add(rawClass);
            }
        }

        throw new IllegalStateException("class is assignable from parent but can not be extracted");
    }

    /**
     * Extracts a specific generic type argument from a class hierarchy by position.
     * <p>
     * This method is a convenience wrapper around {@link #getParentGenericTypes(Class, Class)}
     * that returns a single generic type argument at the specified position.
     * <p>
     * The method includes comprehensive error handling for various edge cases and
     * provides detailed error messages for debugging purposes.
     *
     * @param pos the zero-based position of the generic type argument to extract
     * @param child the child class to analyze
     * @param expectedSuperClass the parent class or interface with generic types to find
     * @return the generic type class at the specified position
     * @throws IllegalArgumentException if the position is negative
     * @throws IndexOutOfBoundsException if the position is beyond the available generic types
     * @throws IllegalStateException if the parent method returns null or the extracted type is null
     * @throws RuntimeException if any other unexpected error occurs during extraction
     */
    public static Class<?> getParentGenericTypes(int pos, @NotNull Class<?> child, @NotNull Class<?> expectedSuperClass) {
        try {
            // Проверяем валидность позиции
            if (pos < 0) {
                throw new IllegalArgumentException("Position cannot be negative: " + pos);
            }

            // Получаем массив типов из родительского метода
            Class<?>[] parentTypes = ReflectionTools.getParentGenericTypes(child, expectedSuperClass);

            // Проверяем, что массив не null
            if (parentTypes == null) {
                throw new IllegalStateException("Parent method returned null");
            }

            // Проверяем, что позиция не выходит за границы массива
            if (pos >= parentTypes.length) {
                throw new IndexOutOfBoundsException(
                    String.format("Position %d is out of bounds for array of length %d", pos, parentTypes.length)
                );
            }

            // Проверяем, что элемент по указанной позиции не null
            Class<?> result = parentTypes[pos];
            if (result == null) {
                throw new IllegalStateException(
                    String.format("Generic type at position %d is null", pos)
                );
            }

            return result;

        } catch (IllegalArgumentException | IndexOutOfBoundsException | IllegalStateException e) {
            // Перебрасываем наши исключения как есть
            throw e;
        } catch (Exception e) {
            // Оборачиваем любые другие исключения
            throw new RuntimeException(
                String.format(
                    "Failed to get generic type at position %d for child %s and parent %s",
                    pos, child.getName(), expectedSuperClass.getName()
                ), e
            );
        }
    }
}
