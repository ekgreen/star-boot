package com.github.old.dog.star.boot.dictionaries.api;

/**
 * An interface for mapping rows, represented as {@link DictionaryRow}, to an instance of type {@code T}.
 *
 * @param <T> the type of the object that the row will be mapped to
 */
public interface DictionaryRowMapper<T> {

    /**
     * Maps a {@link DictionaryRow} to an instance of type {@code T}.
     *
     * @param row the {@link DictionaryRow} to be mapped
     * @return an object of type {@code T} resulting from the mapping of the given row
     */
    T mapRow(DictionaryRow row);
}
