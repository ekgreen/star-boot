package com.github.old.dog.star.boot.reflection.iterable;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code ToIterable} class is an abstract base class for adapting various object types
 * into {@link Iterable} instances. It provides a generic framework for converting objects
 * into an iterable representation, and its subclasses are responsible for implementing the
 * specific logic needed for different object types.
 *
 * @param <T> the type of elements that the resulting {@link Iterable} will contain
 */
public abstract class ToIterable<T> {

    /**
     * Determines whether the given object is applicable for processing by this instance.
     * This method should be used to verify if the provided {@code subject} can be
     * converted into an {@link Iterable} using the {@code makeIterable} method.
     *
     * @param subject the object to check for applicability; may be {@code null}
     * @return {@code true} if the object is applicable for this instance, otherwise {@code false}
     */
    public abstract boolean isApplicable(@Nullable Object subject);

    /**
     * Converts the given {@code subject} into an {@link Iterable} if it is applicable.
     * The applicability of the {@code subject} should be checked using the {@link #isApplicable(Object)}
     * method before invoking this method.
     *
     * @param subject the object to be converted into an {@link Iterable}; may be {@code null}
     * @return an {@link Iterable} representation of the given {@code subject}
     * @throws IllegalArgumentException if the {@code subject} is not applicable for conversion
     */
    public abstract Iterable<T> makeIterable(@Nullable Object subject);

    /**
     * Combines the current {@code ToIterable} instance with another {@code ToIterable} instance
     * to create a composite instance. The resulting instance will use the current instance if
     * it is applicable to the subject; otherwise, it will use the provided {@code other} instance.
     *
     * @param other the fallback {@code ToIterable} instance to be used if the current instance
     *              is not applicable to the subject
     * @return a new {@code ToIterable} instance that determines applicability and creates an
     * {@link Iterable} using the first applicable instance
     */
    public ToIterable<T> ifElse(ToIterable<T> other) {
        return new IfElse<>(this, other);
    }

    /**
     * A private static class that extends {@link ToIterable} to provide conditional
     * behavior for converting objects into {@link Iterable} instances. The class combines
     * the behavior of two {@link ToIterable} instances: {@code origin} and {@code other}.
     * <p>
     * This implementation first checks the applicability of the {@code origin} instance
     * for a given object. If applicable, it delegates the conversion process to the {@code origin}
     * instance. If not, it proceeds to check the {@code other} instance and delegates the
     * conversion to it if applicable.
     *
     * @param <T> the type of elements that the resulting {@link Iterable} will contain
     */
    @RequiredArgsConstructor
    private static class IfElse<T> extends ToIterable<T> {

        private final ToIterable<T> origin;
        private final ToIterable<T> other;

        @Override
        public boolean isApplicable(@Nullable Object subject) {
            return origin.isApplicable(subject) || other.isApplicable(subject);
        }

        @Override
        public Iterable<T> makeIterable(@Nullable Object subject) {
            return origin.isApplicable(subject)
                ? origin.makeIterable(subject) : other.makeIterable(subject);
        }
    }
}
