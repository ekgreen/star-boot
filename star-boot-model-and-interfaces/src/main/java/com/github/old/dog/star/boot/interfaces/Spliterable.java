package com.github.old.dog.star.boot.interfaces;

import java.util.Spliterator;

/**
 * An interface for objects that can be converted to a Spliterator.
 * <p>
 * This interface provides a standard way for objects to expose themselves as
 * Spliterators, which enables them to be used with the Stream API and other
 * operations that accept Spliterators.
 *
 * @param <T>           the type of elements returned by the spliterator
 * @param <SPLITERATOR> the specific type of spliterator to be returned
 */
public interface Spliterable<T, SPLITERATOR extends Spliterator<T>> {

    /**
     * Converts this object to a Spliterator.
     *
     * @return a spliterator over the elements of this object
     */
    SPLITERATOR toSpliterator();
}
