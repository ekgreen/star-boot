package com.github.old.dog.star.boot.toolbox.collections.sequences;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread-safe implementation of {@link InfiniteShortSequence} that uses an atomic counter.
 * <p>
 * This class generates a sequence of short values by incrementing an atomic counter
 * and taking the result modulo {@link Short#MAX_VALUE} to ensure the values stay
 * within the short range. The sequence will cycle after reaching the maximum short value.
 */
public class AtomicInfiniteShortSequence implements InfiniteShortSequence {

    /**
     * The atomic counter used to generate sequence values.
     */
    private final AtomicInteger counter = new AtomicInteger();

    /**
     * {@inheritDoc}
     * <p>
     * This implementation atomically increments the counter and returns the result
     * as a short value, ensuring it wraps around when reaching {@link Short#MAX_VALUE}.
     *
     * @return the next short value in the sequence
     */
    @Override
    public short next() {
        return (short) (counter.getAndIncrement() % Short.MAX_VALUE);
    }
}
