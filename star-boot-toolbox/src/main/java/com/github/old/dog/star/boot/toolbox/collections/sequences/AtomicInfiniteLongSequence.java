package com.github.old.dog.star.boot.toolbox.collections.sequences;

import lombok.RequiredArgsConstructor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe implementation of {@link InfiniteLongSequence} that uses an atomic counter.
 * <p>
 * This class generates a sequence of short values by incrementing an atomic counter
 * and taking the result modulo {@link Long#MAX_VALUE} to ensure the values stay
 * within the short range. The sequence will cycle after reaching the maximum short value.
 */
@RequiredArgsConstructor
public class AtomicInfiniteLongSequence implements InfiniteLongSequence {

    /**
     * The atomic counter used to generate sequence values.
     */
    private final AtomicLong counter;

    /**
     * Constructs an AtomicInfiniteLongSequence with the provided initial value.
     *
     * @param initialValue the initial value of the sequence
     */
    public AtomicInfiniteLongSequence(long initialValue) {
        this(new AtomicLong(initialValue));
    }

    /**
     * Constructs an AtomicInfiniteLongSequence with an initial value of zero.
     * This initializes the internal atomic counter to 0.
     */
    public AtomicInfiniteLongSequence() {
        this(0);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation atomically increments the counter and returns the result
     * as a short value, ensuring it wraps around when reaching {@link Short#MAX_VALUE}.
     *
     * @return the next short value in the sequence
     */
    @Override
    public long next() {
        return (counter.getAndIncrement() % Long.MAX_VALUE);
    }
}
