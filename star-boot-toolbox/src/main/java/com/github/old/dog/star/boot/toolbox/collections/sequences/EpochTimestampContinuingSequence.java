package com.github.old.dog.star.boot.toolbox.collections.sequences;

import lombok.RequiredArgsConstructor;

/**
 * An implementation of {@link InfiniteLongSequence} that generates values based on
 * the time elapsed since a specified epoch.
 * <p>
 * This class produces a continuous sequence of long values by calculating the
 * difference between the current time and a reference epoch, bounded to a specified
 * maximum value. The sequence will wrap around when reaching the bound.
 */
@RequiredArgsConstructor
public class EpochTimestampContinuingSequence implements InfiniteLongSequence {

    /**
     * A predefined system epoch timestamp (approximately November 2023).
     */
    private static final long SYSTEM_EPOCH = 1_700_000_000_000L;

    /**
     * The reference epoch from which time differences are calculated.
     */
    private final long epoch;

    /**
     * The upper bound for the generated values, causing wrap-around.
     */
    private final long bound;

    /**
     * Creates a new sequence using the system epoch and a specified bit length for the bound.
     * <p>
     * The bound is calculated as 2^len, which means the values will wrap around
     * after reaching this power of 2.
     *
     * @param len the bit length determining the bound (2^len)
     * @return a new infinite sequence of time-based values
     */
    public static InfiniteLongSequence systemEpoch(int len) {
        long bound = (long) Math.pow(2, len);
        return new EpochTimestampContinuingSequence(EpochTimestampContinuingSequence.SYSTEM_EPOCH, bound);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation calculates the milliseconds elapsed since the epoch,
     * takes the modulo with the bound, and handles wrap-around to ensure the
     * returned value is always within the range [0, bound).
     *
     * @return the next time-based value in the sequence
     */
    @Override
    public long next() {
        long stamp = (System.currentTimeMillis() - epoch) % bound;

        if (bound - stamp <= 0) {
            stamp = stamp % bound;
        }

        return stamp;
    }
}
