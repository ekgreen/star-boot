package com.github.old.dog.star.boot.reflection.type;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Implementation of the {@link Ref} interface that represents a type reference.
 *
 * This class is used to encapsulate a {@link TypeReference}, allowing for
 * retrieval of the type name that the reference represents. It provides
 * a way to work with generic types at runtime where the type information
 * might otherwise be erased.
 *
 * @param <T> the type encapsulated by this {@code TypeRef}
 */
@RequiredArgsConstructor
public final class TypeRef<T> implements Ref {

    private final TypeReference<T> reference;

    @Override
    public String getRefType() {
        return reference.getType().getTypeName();
    }

}
