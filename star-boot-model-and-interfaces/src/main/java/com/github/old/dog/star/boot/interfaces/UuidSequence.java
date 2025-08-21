package com.github.old.dog.star.boot.interfaces;

import java.util.UUID;

/**
 * A non-sealed interface representing a sequence of UUID values.
 * <p>
 * This interface extends {@link Sequence} with UUID as the specific type,
 * allowing implementations to provide sequences of universally unique identifiers.
 */
public non-sealed interface UuidSequence extends Sequence<UUID> {
}
