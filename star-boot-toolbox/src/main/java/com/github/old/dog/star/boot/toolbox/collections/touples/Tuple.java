package com.github.old.dog.star.boot.toolbox.collections.touples;


import lombok.RequiredArgsConstructor;

/**
 * A generic class to represent a tuple of three elements. The tuple consists
 * of three components, which can be of different or the same types.
 *
 * @param <FIRST>  the type of the first component
 * @param <SECOND> the type of the second component
 * @param <THIRD>  the type of the third component
 */
@RequiredArgsConstructor
public class Tuple<FIRST, SECOND, THIRD> {
    private final FIRST first;
    private final SECOND second;
    private final THIRD third;

    public static <FIRST, SECOND, THIRD> Tuple<FIRST, SECOND, THIRD> of(FIRST first, SECOND second, THIRD third) {
        return new Tuple<>(first, second, third);
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public THIRD getThird() {
        return third;
    }
}
