package com.github.old.dog.star.boot.interfaces;

import com.github.old.dog.star.boot.throwbles.Throwables;
import java.util.function.BiConsumer;

/**
 * A functional interface representing a BiConsumer that can throw exceptions.
 * This interface extends {@link BiConsumer} and provides mechanisms for handling
 * exceptions in lambda expressions where checked exceptions would otherwise
 * need to be caught explicitly.
 *
 * @param <T> the type of the first input argument
 * @param <U> the type of the second input argument
 */
public interface BiExConsumer<T, U> extends BiConsumer<T, U> {

    /**
     * Performs the operation on the given arguments, possibly throwing an exception.
     * This is the main functional method that implementations must override.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @throws Throwable if the operation fails with an exception
     */
    void exceptionalAccept(T t, U u) throws Throwable;

    /**
     * Performs the operation on the given arguments, catching any exceptions and
     * re-throwing them as unchecked exceptions using {@link Throwables#sneakyThrow}.
     * This method delegates to {@link #exceptionalAccept} while handling exceptions.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    @Override
    default void accept(T t, U u) {
        try {
            exceptionalAccept(t, u);
        } catch (Throwable someException) {
            Throwables.sneakyThrow(someException);
        }
    }

    /**
     * Converts this BiExConsumer to a BiExFunction that returns null after performing
     * the consumer operation. This is useful when you need to use a consumer in a
     * context that requires a function.
     *
     * @return a BiExFunction that performs this consumer's operation and returns null
     */
    default BiExFunction<T, U, Void> asFunction() {
        return (t, u) -> {
            BiExConsumer.this.accept(t, u);
            return null;
        };
    }

    /**
     * Creates a BiExConsumer from a standard BiConsumer. This is useful for converting
     * existing BiConsumer instances to the BiExConsumer interface.
     *
     * @param <T>      the type of the first input argument
     * @param <U>      the type of the second input argument
     * @param consumer the BiConsumer to convert
     * @return a BiExConsumer that delegates to the provided BiConsumer
     */
    public static <T, U> BiExConsumer<T, U> beExceptional(BiConsumer<T, U> consumer) {
        return consumer::accept;
    }
}
