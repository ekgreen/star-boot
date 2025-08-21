package com.github.old.dog.star.boot.interfaces;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single {@code long}-valued argument and
 * returns no result.  This is the primitive type specialization of
 * {@link Consumer} for {@code long}.  Unlike most other functional interfaces,
 * {@code IntConsumer} is expected to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(long)}.
 *
 * @see Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface LongConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param value the input argument
     */
    void accept(long value);

    /**
     * Returns a composed {@code IntConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code IntConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default LongConsumer andThen(LongConsumer after) {
        Objects.requireNonNull(after);
        return (long t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
