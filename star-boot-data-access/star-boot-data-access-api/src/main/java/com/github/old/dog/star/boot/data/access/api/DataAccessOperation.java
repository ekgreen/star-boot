package com.github.old.dog.star.boot.data.access.api;


import com.github.old.dog.star.boot.throwbles.Throwables;

/**
 * Represents a functional interface that defines an operation to be performed using a JOOQ repository.
 * This interface provides a mechanism to execute an operation that may throw an exception and includes
 * helper methods for handling void operations and exception management.
 *
 * @param <O> The type of the result returned by the operation.
 * @param <R> The type of the repository object on which the operation is performed.
 */
@FunctionalInterface
public interface DataAccessOperation<O, R> {

    /**
     * Performs an operation using the provided repository and returns a result.
     *
     * @param repo the repository instance used to perform the operation
     * @return the result of the operation
     * @throws Exception if an error occurs during the operation
     */
    O doOperation(R repo) throws Exception;

    // ================================================================================================================================== //

    /**
     * Executes an operation on the provided repository that may throw an exception,
     * handling the exception by rethrowing it as an unchecked exception.
     *
     * @param repo the repository instance on which the operation is performed
     * @return the result of the operation, or null if an exception occurs
     */
    default O doThrowableOperation(R repo) {
        O result = null;

        try {
            result = doOperation(repo);
        } catch (Exception exception) {
            Throwables.sneakyThrow(exception);
        }

        return result;
    }

    // ================================================================================================================================== //

    /**
     * Wraps a {@link VoidOperation} into a {@link DataAccessOperation} that executes the void operation
     * and returns null. This method is used to adapt void operations to the {@link DataAccessOperation} interface.
     *
     * @param <R>       The type of the repository object on which the void operation is performed.
     * @param operation The void operation to be executed.
     * @return A {@link DataAccessOperation} instance that executes the specified void operation and returns null.
     */
    static <R> DataAccessOperation<Void, R> voidOperation(VoidOperation<R> operation) {
        return repo -> {
            operation.execute(repo);
            return null;
        };
    }

    /**
     * Represents a functional interface for operations that do not return a result and may
     * throw exceptions. This interface is typically used to define void actions that operate
     * on a specific type of repository without producing a return value.
     *
     * @param <R> The type of the repository object on which the operation is performed.
     */
    @FunctionalInterface
    interface VoidOperation<R> {
        void execute(R repo) throws Exception;
    }

}
