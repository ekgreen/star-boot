package com.github.old.dog.star.boot.data.access.jooq.api.dao;

import com.github.old.dog.star.boot.data.access.jooq.api.JooqOperation;
import org.jooq.DAO;
import org.jooq.TableRecord;

/**
 * An interface that represents a specific type of Jooq-based operation that works with a DAO
 * (Data Access Object) repository. It enhances the functionality of the {@link JooqOperation}
 * interface by providing an extended contract tailored for operations involving Jooq's DAO capabilities.
 *
 * @param <O>    The type of the result returned by the DAO operation.
 * @param <R>    The type of the {@link TableRecord} entity in the Jooq repository.
 * @param <P>    The type of the POJO (Plain Old Java Object) associated with the repository.
 * @param <T>    The type of the primary key or identifier of the entities managed by the repository.
 * @param <REPO> The type of the Jooq {@link DAO} repository used in the operation.
 */
public interface JooqDaoOperation<O, R extends TableRecord<R>, P, T, REPO extends DAO<R, P, T>>
    extends JooqOperation<O, REPO> {

    /**
     * Creates a JooqDaoOperation instance for void DAO operations.
     * This method wraps the provided VoidDaoOperation into a JooqDaoOperation
     * that executes the void operation and returns null.
     *
     * @param <R>       The type of the {@link TableRecord} entity in the DAO repository.
     * @param <P>       The type of the POJO (Plain Old Java Object) associated with the DAO repository.
     * @param <T>       The type of the primary key or identifier managed by the DAO repository.
     * @param <REPO>    The type of the DAO repository used in the operation.
     * @param operation The void DAO operation to be executed.
     * @return A JooqDaoOperation instance that performs the specified void DAO operation
     * and returns null.
     */
    static <R extends TableRecord<R>, P, T, REPO extends DAO<R, P, T>> JooqDaoOperation<Void, R, P, T, REPO> voidOperation(
        VoidDaoOperation<R, P, T, REPO> operation
    ) {
        return repo -> {
            operation.execute(repo);
            return null;
        };
    }

    /**
     * Functional interface representing a DAO operation that does not return a result.
     * This interface is designed for operations that interact with a Jooq-based DAO
     * (Data Access Object) and perform actions without returning a value.
     *
     * @param <R>    The type of the {@link TableRecord} entity in the DAO repository.
     * @param <P>    The type of the POJO (Plain Old Java Object) associated with the DAO repository.
     * @param <T>    The type of the primary key or identifier managed by the DAO repository.
     * @param <REPO> The type of the Jooq {@link DAO} repository used in the operation.
     */
    @FunctionalInterface
    interface VoidDaoOperation<R extends TableRecord<R>, P, T, REPO extends DAO<R, P, T>> {
        void execute(REPO repo) throws Exception;
    }

}
