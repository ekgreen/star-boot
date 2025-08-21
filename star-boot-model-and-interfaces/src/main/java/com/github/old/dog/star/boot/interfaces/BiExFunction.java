package com.github.old.dog.star.boot.interfaces;

import com.github.old.dog.star.boot.throwbles.Throwables;
import java.util.function.BiFunction;

/**
 * A functional interface representing a BiFunction that can throw exceptions.
 * This interface extends {@link BiFunction} and provides mechanisms for handling
 * exceptions in lambda expressions where checked exceptions would otherwise
 * need to be caught explicitly.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 * @param <R> the type of the result
 */
public interface BiExFunction<T, U, R> extends BiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments, possibly throwing an exception.
     * This is the main functional method that implementations must override.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return the function result
     * @throws Throwable if the function application fails with an exception
     */
    R exceptionalApply(T t, U u) throws Throwable;

    /**
     * Applies this function to the given arguments, catching any exceptions and
     * re-throwing them as unchecked exceptions using {@link Throwables#sneakyThrow}.
     * This method delegates to {@link #exceptionalApply} while handling exceptions.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @return the function result
     */
    @Override
    default R apply(T t, U u) {
        R value = null;

        try {
            value = exceptionalApply(t, u);
        } catch (Throwable someException) {
            Throwables.sneakyThrow(someException);
        }

        return value;
    }

    /**
     * Creates a BiExFunction from a standard BiFunction. This is useful for converting
     * existing BiFunction instances to the BiExFunction interface.
     *
     * @param <T> the type of the first input argument
     * @param <U> the type of the second input argument
     * @param <R> the type of the result
     * @param consumer the BiFunction to convert
     * @return a BiExFunction that delegates to the provided BiFunction
     */
    public static <T, U, R> BiExFunction<T, U, R> beExceptional(BiFunction<T, U, R> consumer) {
        return consumer::apply;
    }
}
