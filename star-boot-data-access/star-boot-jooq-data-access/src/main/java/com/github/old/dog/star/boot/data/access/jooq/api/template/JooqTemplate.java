package com.github.old.dog.star.boot.data.access.jooq.api.template;

import com.github.old.dog.star.boot.data.access.jooq.api.dao.JooqDaoOperation;
import com.github.old.dog.star.boot.data.access.jooq.api.dsl.JooqDslOperation;
import org.jetbrains.annotations.NotNull;
import org.jooq.DAO;
import org.jooq.DSLContext;
import org.jooq.TableRecord;
import java.util.List;

/**
 * Represents a template interface for Jooq-based database operations.
 * This interface provides utility methods to handle transactional operations,
 * atomic and simple DSL/DAO operations, and batch processing.
 *
 * @param <RECORD> Represents the type of the {@link TableRecord} handled by the DAO repository.
 * @param <POJO>   The type of the POJO (Plain Old Java Object) associated with the repository.
 * @param <ID>     The type of the primary key or identifier for entities managed by the repository.
 * @param <REPO>   The type of the DAO repository extending {@link DAO}, used for data access operations.
 */
public interface JooqTemplate<RECORD extends TableRecord<RECORD>, POJO, ID, REPO extends DAO<RECORD, POJO, ID>> {

    // ====================================== Core operations =========================================================================== //

    /**
     * Executes a database operation within a transactional context.
     * The provided operation is expected to define all actions to be performed
     * within the same transaction boundary. Any exception occurring during the
     * execution will result in a rollback of the transaction.
     *
     * @param operation the transactional operation to be executed; must implement {@link JooqTransactionalOperation}
     */
    void transactional(JooqTransactionalOperation operation);

    /**
     * Executes a database operation atomically without transactional context.
     * This method ensures the provided operation executes as a single logical unit of work.
     * The operation is carried out using JOOQ's {@link DSLContext}, allowing custom database interactions.
     *
     * @param <O>       The type of the result produced by the execution of the operation.
     *                  This can range from domain-specific entities, collections, or any other type.
     * @param operation The DSL-based database operation to be performed.
     *                  Must be a valid implementation of {@link JooqDslOperation}.
     * @return The result of the executed operation, of type O
     */
    <O> O atomicOperation(JooqDslOperation<O> operation);

    /**
     * Executes a DAO-driven database operation atomically. This method ensures that the
     * provided operation is performed as a single logical unit of work leveraging the capabilities
     * of a Jooq-based DAO. The operation is defined by the {@link JooqDaoOperation} functional
     * interface, which allows custom logic to be executed using the DAO repository.
     *
     * @param <O>       The type of the result returned by the DAO operation. It can range from
     *                  custom domain objects to collections or other types.
     * @param operation The DAO-based operation to be performed atomically. Must be a
     *                  valid implementation of {@link JooqDaoOperation}.
     * @return The result of the operation, of type O
     */
    <O> O atomicOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation);

    /**
     * Executes a database operation using the provided JOOQ's {@link DSLContext}.
     * This method allows for defining custom logic to interact with the database
     * using a functional interface implementation of {@link JooqDslOperation}.
     *
     * @param <O>       The type of the result produced by the operation. It can range
     *                  from specific domain objects, collections, or other data types.
     * @param operation The DSL-based operation to be executed. Must implement {@link JooqDslOperation}.
     * @return The result of the executed operation, of type O
     */
    <O> O simpleOperation(JooqDslOperation<O> operation);

    /**
     * Executes a DAO-based database operation using a Jooq DAO repository. The operation
     * is defined as a {@link JooqDaoOperation} functional interface, allowing custom data-access
     * logic involving the repository. This method ensures the operation is performed as a single
     * logical unit of work.
     *
     * @param <O>       The type of the result returned by the DAO operation.
     *                  This can be a domain object, a collection, or any other type.
     * @param operation The DAO-based operation to be executed.
     *                  Must be an implementation of {@link JooqDaoOperation}.
     * @return The result of the operation, of type O
     */
    <O> O simpleOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation);

    // ====================================== Quick Access Operations =================================================================== //

    /**
     * Generates the next unique identifier based on the specified sequence key.
     * This method utilizes the provided sequence key to determine the corresponding
     * database sequence or mechanism used for ID generation.
     *
     * @param sequenceKey the key associated with the sequence used to generate the next ID; must not be null
     * @return the next generated unique identifier of type ID
     */
    ID generateNextId(@NotNull String sequenceKey);

    /**
     * Inserts a batch of provided POJOs into the database. The method assumes all entities in the
     * list are new and will be persisted as separate insert statements or a single bulk operation
     * depending on the underlying implementation.
     *
     * @param forInsert a list of POJOs to be inserted into the database; must not be null or empty
     */
    void batchInsert(List<POJO> forInsert);

    /**
     * Updates a batch of provided POJOs in the database. The method assumes that all entities
     * in the list already exist in the database and need to be updated. The updates are executed
     * as separate statements or as a batch operation, depending on the underlying implementation.
     *
     * @param forUpdate a list of POJOs to be updated in the database; must not be null or empty
     */
    void batchUpdate(List<POJO> forUpdate);
}
