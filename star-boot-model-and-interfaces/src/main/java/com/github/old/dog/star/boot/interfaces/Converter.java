package com.github.old.dog.star.boot.interfaces;

import java.util.function.Function;

/**
 * A functional interface for converting data from one type to another.
 * <p>
 * This interface extends Java's standard {@link Function} interface and provides
 * additional composition methods that maintain the Converter type throughout the chain.
 * It's designed for transforming data between different representations, such as
 * converting between domain objects and DTOs, serializing/deserializing data formats,
 * or transforming between different data structures.
 * <p>
 * Implementations should be immutable and thread-safe whenever possible.
 *
 * @param <T> the source type of the conversion
 * @param <R> the target type of the conversion
 */
public interface Converter<T, R> extends Function<T, R> {
    /**
     * Converts the provided value from the source type to the target type.
     * <p>
     * This is the primary method of the Converter interface that implementations
     * must provide to define their conversion logic.
     *
     * @param value the source value to convert
     * @return the converted target value
     * @throws IllegalArgumentException if the source value cannot be converted
     */
    R convert(T value);

    // ================================================================================================================================== //

    /**
     * Returns a composed converter that first applies the {@code composeConverter}
     * to its input, and then applies this converter to the result.
     * <p>
     * This method allows for the functional composition of converters, similar to
     * function composition in mathematics.
     *
     * @param <O> the type of input to the {@code composeConverter}
     * @param composeConverter the converter to apply before this converter
     * @return a composed converter that first applies the {@code composeConverter}
     *         and then applies this converter
     * @throws NullPointerException if {@code composeConverter} is null
     */
    default <O> Converter<O, R> before(Converter<O, T> composeConverter) {
        return data -> this.convert(composeConverter.convert(data));
    }

    /**
     * Returns a composed converter that first applies this converter to its input,
     * and then applies the {@code andConverter} to the result.
     * <p>
     * This method allows for the functional composition of converters, creating
     * processing pipelines that transform data through multiple steps.
     *
     * @param <O> the type of output of the {@code andConverter}
     * @param andConverter the converter to apply after this converter
     * @return a composed converter that first applies this converter and then
     *         applies the {@code andConverter}
     * @throws NullPointerException if {@code andConverter} is null
     */
    default <O> Converter<T, O> after(Converter<R, O> andConverter) {
        return data -> andConverter.convert(this.convert(data));
    }

    /**
     * Applies this converter to the given input.
     * <p>
     * This method implements the {@link Function#apply} method from the
     * {@link Function} interface by delegating to the {@link #convert} method.
     * This allows Converter instances to be used directly in contexts that
     * expect a Function.
     *
     * @param t the input to the converter
     * @return the result of applying the conversion
     */
    @Override
    default R apply(T t) {
        return convert(t);
    }

}
