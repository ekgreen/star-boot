package com.github.old.dog.star.boot.toolbox.collections.sequences;


import com.github.old.dog.star.boot.interfaces.IntSequence;
import com.github.old.dog.star.boot.interfaces.Spliterable;
import java.util.Spliterator;
import java.util.Spliterator.OfInt;

/**
 * An interface representing an infinite sequence of integer values.
 * <p>
 * This interface extends both {@link IntSequence} and {@link Spliterable}, providing
 * the ability to generate an unlimited stream of integer values and to convert the
 * sequence to a spliterator for use with the Java Stream API.
 */
public interface InfiniteIntSequence extends Spliterable<Integer, OfInt>, IntSequence {

    /**
     * Retrieves the next integer value in the sequence.
     * <p>
     * This is a primitive specialized version of {@link #nextValue()}, which avoids
     * boxing the primitive int value.
     *
     * @return the next integer value
     */
    int next();

    /**
     * {@inheritDoc}
     * <p>
     * This implementation delegates to {@link #next()} and boxes the result.
     *
     * @return the next integer value, boxed as an Integer
     */
    @Override
    default Integer nextValue() {
        return next();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation creates a new {@link Spliterators.Infinite.OfInt} instance
     * backed by this sequence.
     *
     * @return a spliterator over the elements of this sequence
     */
    @Override
    default Spliterator.OfInt toSpliterator() {
        return new Spliterators.Infinite.OfInt(this);
    }
}
