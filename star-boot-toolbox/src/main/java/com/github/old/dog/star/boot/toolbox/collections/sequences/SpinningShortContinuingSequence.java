package com.github.old.dog.star.boot.toolbox.collections.sequences;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;

/**
 * A thread-safe implementation of {@link InfiniteShortSequence} that uses a spinning
 * mechanism to generate bounded short values with time-based periods.
 * <p>
 * This class creates a new generator for each time period and ensures that values
 * stay below a specified bound. If a value exceeds the bound, it waits for the next
 * time period. The time periods are determined by dividing timestamps by a frequency factor.
 */
@RequiredArgsConstructor
public class SpinningShortContinuingSequence implements InfiniteShortSequence {

    /**
     * The frequency factor used to determine time periods (shift right by this value).
     */
    private final int frequency;

    /**
     * The upper bound for generated short values.
     */
    private final short bound;

    /**
     * The timestamp sequence used for determining time periods.
     */
    private final InfiniteLongSequence timestamp = EpochTimestampContinuingSequence
            .systemEpoch(Integer.MAX_VALUE);

    /**
     * Lock for thread-safe generator updates.
     */
    private final Lock lock
            = new ReentrantLock();

    /**
     * The timestamp of the current period.
     */
    private volatile long pinnedAt;

    /**
     * The current generator for the current period.
     */
    private volatile InfiniteShortSequence generator;

    /**
     * Creates a new sequence with the specified frequency and bound.
     *
     * @param frequency the frequency factor for time periods
     * @param bound the upper bound for generated values
     * @return a new spinning short sequence
     */
    public static SpinningShortContinuingSequence timeBound(int frequency, int bound) {
        return new SpinningShortContinuingSequence(frequency, (short) bound);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation delegates to {@link #spinAndGet()}.
     *
     * @return the next short value in the sequence
     */
    @Override
    public short next() {
        return spinAndGet();
    }

    /**
     * Core method that implements the spinning mechanism to get the next value.
     * <p>
     * This method checks if the current time period has changed, creates a new generator
     * if necessary, and ensures the generated value is below the bound. If the value
     * exceeds the bound, it spins until the next time period.
     *
     * @return a short value below the bound
     */
    private short spinAndGet() {
        long current = timestamp.next() >> frequency;

        while (true) {
            if (pinnedAt < current) {
                lock.lock();

                try {
                    this.pinnedAt = current;
                    this.generator = createNewGenerator();
                } finally {
                    lock.unlock();
                }
            }

            short value = this.generator.next();

            if (value < bound) {
                return value;
            }

            do {
                current = timestamp.next() >> frequency;
                Thread.onSpinWait();
            } while (pinnedAt == current);
        }
    }

    /**
     * Creates a new generator for the current time period.
     *
     * @return a new infinite short sequence generator
     */
    private InfiniteShortSequence createNewGenerator() {
        return new AtomicInfiniteShortSequence();
    }
}
