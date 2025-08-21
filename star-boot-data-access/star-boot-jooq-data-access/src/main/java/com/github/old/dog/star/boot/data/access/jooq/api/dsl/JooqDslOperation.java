package com.github.old.dog.star.boot.data.access.jooq.api.dsl;

import com.github.old.dog.star.boot.data.access.jooq.api.JooqOperation;
import org.jooq.DSLContext;

/**
 * Represents an operation performed using JOOQ's {@link DSLContext}.
 * This interface extends {@link JooqOperation}, specifically targeting operations
 * where the repository type is defined as {@link DSLContext}.
 *
 * @param <O> The type of the result produced by the execution of this operation.
 *            This can range from a specific entity, a collection of entities, or any other type,
 *            depending on the implementation.
 */
public interface JooqDslOperation<O> extends JooqOperation<O, DSLContext> {

    /**
     * Creates a JooqDslOperation that executes a void operation using a DSLContext without returning a result.
     *
     * @param operation The void operation to execute, represented by a VoidDslOperation, which performs the operation
     *                  against the provided DSLContext.
     * @return A JooqDslOperation of void that wraps the provided VoidDslOperation for execution.
     */
    static JooqDslOperation<Void> voidOperation(VoidDslOperation operation) {
        return dsl -> {
            operation.execute(dsl);
            return null;
        };
    }

    /**
     * Represents a functional interface for performing void operations using JOOQ's {@link DSLContext}.
     * This interface is typically used for actions that interact with the database but do not
     * return a value.
     * <p>
     * The {@code VoidDslOperation} interface is designed to incorporate DSL-based database operations
     * within a functional programming style, providing flexibility in managing transactional or
     * non-transactional DSL logic.
     * <p>
     * Functional Interface: This annotation indicates that the interface is intended to be used
     * as a functional interface, making it suitable for lambda expressions and method references.
     * <p>
     * Method Overview:
     * - {@link #execute(DSLContext)}: Defines the core logic of the void operation,
     * leveraging the provided {@link DSLContext}.
     * <p>
     * Error Handling:
     * The {@link #execute(DSLContext)} method signature includes the {@code throws Exception} clause,
     * allowing implementers to handle or propagate any exceptions arising from database interactions.
     */
    @FunctionalInterface
    interface VoidDslOperation {
        void execute(DSLContext dsl) throws Exception;
    }

}
