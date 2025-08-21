package com.github.old.dog.star.boot.data.access.jooq.api;


import com.github.old.dog.star.boot.data.access.api.DataAccessOperation;

/**
 * Represents a functional interface that defines an operation to be performed using a JOOQ repository.
 * This interface provides a mechanism to execute an operation that may throw an exception and includes
 * helper methods for handling void operations and exception management.
 *
 * @param <O> The type of the result returned by the operation.
 * @param <R> The type of the repository object on which the operation is performed.
 */
@FunctionalInterface
public interface JooqOperation<O, R> extends DataAccessOperation<O, R> {
}
