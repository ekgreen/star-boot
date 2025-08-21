package com.github.old.dog.star.boot.dictionaries.api;

/**
 * Defines a contract for mapping attributes from a dictionary or object to a specified type.
 *
 * @param <T> the type of object to which attributes will be mapped
 */
public interface DictionaryAttributeMapper<T> {

    /**
     * Maps the given attribute to an object of type T based on the specified type.
     *
     * @param attribute the attribute to be mapped
     * @param type an integer representing the type or context for mapping
     * @return an object of type T representing the mapped attribute
     */
    T mapAttribute(Object attribute, int type);
}
