package com.github.old.dog.star.boot.toolbox.collections.sequences;


import com.github.old.dog.star.boot.interfaces.Spliterable;
import com.github.old.dog.star.boot.toolbox.collections.sequences.Spliterators.OfShort;

/**
 * An interface representing an infinite sequence of short values.
 * <p>
 * This interface extends {@link Spliterable}, providing the ability to generate
 * an unlimited stream of short values and to convert the sequence to a specialized
 * spliterator for use with the Java Stream API.
 */
public interface InfiniteShortSequence extends Spliterable<Short, OfShort> {

    /**
     * Retrieves the next short value in the sequence.
     * <p>
     * This is a primitive specialized method that avoids boxing the short value.
     *
     * @return the next short value
     */
    short next();

    /**
     * {@inheritDoc}
     * <p>
     * This implementation creates a new {@link Spliterators.Infinite.OfShort} instance
     * backed by this sequence.
     *
     * @return a spliterator over the elements of this sequence
     */
    @Override
    default Spliterators.OfShort toSpliterator() {
        return new Spliterators.Infinite.OfShort(this);
    }
}
