package com.github.old.dog.star.boot.data.access.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @JavaDoc is required [todo]
 */
public interface DataAccessTemplate<POJO, RECORD, ID, REPO> {

    // ====================================== Core operations =========================================================================== //

    /**
     * Executes a database operation within a transactional context.
     * The provided operation is expected to define all actions to be performed
     * within the same transaction boundary. Any exception occurring during the
     * execution will result in a rollback of the transaction.
     *
     * @param operation the transactional operation to be executed; must implement {@link TransactionalDataAccessOperation}
     */
    void transactional(TransactionalDataAccessOperation operation);

    <O> O atomicOperation(DataAccessOperation<O, REPO> operation);

    <O> O simpleOperation(DataAccessOperation<O, REPO> operation);

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
