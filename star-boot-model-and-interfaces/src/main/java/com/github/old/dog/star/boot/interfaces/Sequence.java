package com.github.old.dog.star.boot.interfaces;

/**
 * A sealed interface representing a sequence of values that can be consumed sequentially.
 * <p>
 * This interface serves as the base for various sequence types in the system, providing
 * a common contract for generating sequential values. It is implemented by specific
 * types of sequences for different value types.
 *
 * @param <T> the type of values produced by this sequence
 */
public sealed interface Sequence<T>
    permits IntSequence, LongSequence, ShortSequence, UuidSequence {

    /**
     * Retrieves the next value in the sequence.
     *
     * @return the next value in the sequence
     */
    T nextValue();
}
