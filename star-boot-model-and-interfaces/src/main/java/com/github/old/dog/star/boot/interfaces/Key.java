package com.github.old.dog.star.boot.interfaces;

/**
 * Represents a unique identifier component that provides a method to retrieve
 * a key as a string. Classes or interfaces implementing this interface are
 * expected to ensure that the key uniquely identifies an entity or resource
 * within its context.
 */
public interface Key {

    /**
     * Retrieves the unique key associated with an entity or resource.
     *
     * @return a string representation of the unique key
     */
    String getKey();
}
