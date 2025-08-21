package com.github.old.dog.star.boot.data.access.jooq.api.template;

import com.github.old.dog.star.boot.data.access.jooq.api.JooqOperation;

/**
 * Represents an interface that defines a JOOQ-based transactional operation.
 * This interface extends the {@link JooqOperation} interface with specific
 * behavior for transactional operations that do not require input parameters
 * or return values.
 * <p>
 * The primary purpose of this interface is to allow the implementation of
 * self-contained transactional operations. It ensures that the transactional
 * logic is executed within the context of the overridden {@code doOperation}
 * method.
 * <p>
 * Implementations of this interface are expected to provide a concrete implementation
 * of the {@code transactional()} method. Additionally, it offers a default implementation
 * for handling throwable operations by delegating to the parent interface.
 */
public interface JooqTransactionalOperation extends JooqOperation<Void, Void> {

    /**
     * Executes a transactional operation within the context of the implementing class.
     * <p>
     * The method is designed to encapsulate all actions that should be executed within
     * a single transaction boundary. Implementations should define the required logic
     * for the transaction, ensuring proper exception handling and resource management.
     * <p>
     * Any exception thrown during the execution of this method should be appropriately
     * handled to maintain the transactional integrity and prevent partial updates or
     * corrupted states. Implementations of this method are expected to rely on a
     * transactional framework or infrastructure to enforce transactional behavior.
     *
     * @throws Exception if any error occurs during the execution of the transactional operation
     */
    void transactional() throws Exception;

    // ================================================================================================================================== //

    /**
     * Executes a transactional operation defined by the `transactional` method and returns null.
     * This method serves as the default implementation for performing a JOOQ-based operation
     * within a transactional context.
     *
     * @param repo an input parameter of type Void, which is unused in this implementation
     * @return always returns null as this operation does not produce a result
     * @throws Exception if any error occurs during the transactional operation
     */
    @Override
    default Void doOperation(Void repo) throws Exception {
        transactional();
        return null;
    }

    /**
     * Executes an operation on the provided repository that may throw an exception,
     * delegating the execution to the JooqOperation's default implementation of
     * {@code doThrowableOperation}.
     *
     * @param repo the repository instance on which the operation is performed
     * @return the result of the operation, or null if the operation does not produce a result
     */
    @Override
    default Void doThrowableOperation(Void repo) {
        return JooqOperation.super.doThrowableOperation(repo);
    }
}
