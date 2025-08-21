package com.github.old.dog.star.boot.toolbox.collections.sequences;

import com.github.old.dog.star.boot.interfaces.LongSequence;
import com.github.old.dog.star.boot.interfaces.Spliterable;
import java.util.Spliterator;
import java.util.Spliterator.OfLong;


/**
 * An interface representing an infinite sequence of long values.
 * <p>
 * This interface extends both {@link LongSequence} and {@link Spliterable}, providing
 * the ability to generate an unlimited stream of long values and to convert the
 * sequence to a spliterator for use with the Java Stream API.
 */
public interface InfiniteLongSequence extends Spliterable<Long, OfLong>, LongSequence {

    /**
     * Retrieves the next long value in the sequence.
     * <p>
     * This is a primitive specialized version of {@link #nextValue()}, which avoids
     * boxing the primitive long value.
     *
     * @return the next long value
     */
    long next();

    /**
     * {@inheritDoc}
     * <p>
     * This implementation delegates to {@link #next()} and boxes the result.
     *
     * @return the next long value, boxed as a Long
     */
    @Override
    default Long nextValue() {
        return next();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation creates a new {@link Spliterators.Infinite.OfLong} instance
     * backed by this sequence.
     *
     * @return a spliterator over the elements of this sequence
     */
    @Override
    default Spliterator.OfLong toSpliterator() {
        return new Spliterators.Infinite.OfLong(this);
    }
}
