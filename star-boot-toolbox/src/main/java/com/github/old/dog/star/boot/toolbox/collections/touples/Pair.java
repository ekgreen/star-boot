package com.github.old.dog.star.boot.toolbox.collections.touples;


import lombok.RequiredArgsConstructor;

/**
 * A generic class to represent a pair of values. The pair consists of two
 * components - a left component and a right component, which can be of
 * different types.
 *
 * @param <L> the type of the left component
 * @param <R> the type of the right component
 */
@RequiredArgsConstructor
public class Pair<L, R> {
    private final L left;
    private final R right;

    public static <K extends Comparable<K>, V> Pair<K, V> keyValue(K key, V value) {
        return new Pair<>(key, value);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public L getKey() {
        return getLeft();
    }

    public R getValue() {
        return getRight();
    }

    public String concat(String separator) {
        return left + separator + right;
    }
}
