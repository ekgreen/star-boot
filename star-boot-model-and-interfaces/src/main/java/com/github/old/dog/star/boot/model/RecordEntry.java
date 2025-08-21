package com.github.old.dog.star.boot.model;

import java.util.Map;

/**
 * A record implementation of {@link Map.Entry} representing a key-value pair, used internally
 * for creating map-like structures while maintaining certain immutability guarantees.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 *            <p>
 *            The {@code DefaultEntry} record provides methods to access the key and value of the entry
 *            while disallowing modification of the value. This is particularly useful in cases where
 *            entries need to be immutable or changes should not be allowed.
 *            <p>
 *            It provides default implementations for:
 *            - {@link #getKey()} to retrieve the key
 *            - {@link #getValue()} to retrieve the value
 *            - {@link #setValue(Object)} to throw an {@link UnsupportedOperationException} to prohibit
 *            mutation of the entry value
 */
public record RecordEntry<K, V>(K key, V value) implements Map.Entry<K, V> {

    @Override
    public K getKey() {
        return null;
    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("unable to change entry value");
    }
}
