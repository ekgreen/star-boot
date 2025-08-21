package com.github.old.dog.star.boot.toolbox.functional;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A predicate implementation that maintains a collection of elements that did not pass the test.
 * <p>
 * This class wraps an existing predicate and keeps track of all elements for which
 * the predicate returned false. This is useful for collecting filtered-out elements
 * for later processing or analysis.
 *
 * @param <T> the type of elements tested by this predicate
 */
@RequiredArgsConstructor
public class PredicateWithHolder<T> implements Predicate<T> {

    /**
     * The original predicate that determines whether elements pass the test.
     */
    private final Predicate<T> predicate;

    /**
     * A list containing all elements that did not pass the predicate test.
     */
    @Getter
    private final List<T> outdated = new ArrayList<>();

    /**
     * Tests the specified element against the predicate and adds it to the
     * outdated list if it fails the test.
     *
     * @param t the element to test
     * @return true if the element passes the test, false otherwise
     */
    @Override
    public boolean test(T t) {
        boolean test = predicate.test(t);

        if (!test) {
            outdated.add(t);
        }

        return test;
    }
}
