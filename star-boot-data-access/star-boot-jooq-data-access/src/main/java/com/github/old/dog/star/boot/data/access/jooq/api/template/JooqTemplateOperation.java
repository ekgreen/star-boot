package com.github.old.dog.star.boot.data.access.jooq.api.template;

import com.github.old.dog.star.boot.data.access.jooq.api.JooqOperation;
import org.jooq.DAO;
import org.jooq.TableRecord;

/**
 * Represents an extension of the {@link JooqOperation} interface designed for operations
 * that work specifically with a {@link JooqTemplate}. This interface allows for
 * flexible execution of Jooq-based operations by providing the ability to interact with
 * a template and repository in a type-safe way.
 *
 * @param <O>    The type of the result returned by the operation.
 * @param <R>    The type of the table record associated with the repository.
 * @param <P>    The type of the primary key or identifier for the domain entity.
 * @param <T>    The type of the domain entity managed by the repository.
 * @param <REPO> The type of the DAO (Data Access Object) that is used to perform
 *               operations on the underlying data.
 */
public interface JooqTemplateOperation<O, R extends TableRecord<R>, P, T, REPO extends DAO<R, P, T>>
    extends JooqOperation<O, JooqTemplate<R, P, T, REPO>> {

    /**
     * Creates a `JooqTemplateOperation` for a void operation defined by the provided `VoidTemplateOperation`.
     * The resulting operation executes the logic in the given `VoidTemplateOperation` and returns `null`.
     *
     * @param <R>       The type of the table record associated with the repository.
     * @param <P>       The type of the primary key or identifier for the domain entity.
     * @param <T>       The type of the domain entity managed by the repository.
     * @param <REPO>    The type of the DAO (Data Access Object) used for interacting with the data source.
     * @param operation The void template operation to execute.
     * @return A `JooqTemplateOperation` that executes the provided void operation and returns `null`.
     */
    static <R extends TableRecord<R>, P, T, REPO extends DAO<R, P, T>> JooqTemplateOperation<Void, R, P, T, REPO> voidOperation(
        VoidTemplateOperation<R, P, T, REPO> operation
    ) {
        return template -> {
            operation.execute(template);
            return null;
        };
    }

    /**
     * Represents an operation that performs an action on a {@link JooqTemplate} without returning
     * a result. This functional interface is specifically designed for void operations executed
     * within the context of a JOOQ-based data repository, provided through {@link DAO}.
     *
     * @param <R>    The type of the table record associated with the repository.
     * @param <P>    The type of the primary key or identifier for the domain entity.
     * @param <T>    The type of the domain entity managed by the repository.
     * @param <REPO> The type of the DAO (Data Access Object) used for performing operations
     *               on the underlying data.
     */
    @FunctionalInterface
    interface VoidTemplateOperation<R extends TableRecord<R>, P, T, REPO extends DAO<R, P, T>> {
        void execute(JooqTemplate<R, P, T, REPO> template) throws Exception;
    }

}
