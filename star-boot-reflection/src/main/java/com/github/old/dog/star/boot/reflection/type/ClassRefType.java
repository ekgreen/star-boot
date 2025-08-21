package com.github.old.dog.star.boot.reflection.type;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link Ref} interface that represents a class reference.
 *
 * This class is used to encapsulate a {@link Class}, allowing for retrieval of the
 * class name as the type of reference. It provides a way to work with class-based
 * references at runtime.
 *
 * @param <T> the type represented by the encapsulated class
 */
@RequiredArgsConstructor
public final class ClassRefType<T> implements Ref {

    private final Class<T> clazz;

    @Override
    public String getRefType() {
        return clazz.getSimpleName();
    }
}
