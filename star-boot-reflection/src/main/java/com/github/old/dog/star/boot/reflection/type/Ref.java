package com.github.old.dog.star.boot.reflection.type;

/**
 * A sealed interface representing a reference that may be of various types.
 * <p>
 * The Ref interface provides a common contract for different types of references
 * (e.g., class-based references or generic type references). Implementations
 * of this interface define the logic for identifying the type of reference.
 * <p>
 * This interface is implemented by {@code ClassRefType} and {@code TypeRef}.
 */
public sealed interface Ref permits ClassRefType, TypeRef {

    /**
     * Retrieves the type of the reference as a string.
     *
     * @return a string representation of the type of reference. This may vary
     *         based on the implementation, such as a class name or type name.
     */
    String getRefType();
}
