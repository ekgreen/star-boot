package com.github.old.dog.star.boot.data.access.jooq.api.factories;

import org.jooq.Configuration;
import org.jooq.DAO;
import org.jooq.TableRecord;

/**
 * A factory interface for creating JOOQ repository instances. This interface
 * provides an abstraction that allows for dynamic creation of repository
 * implementations based on a configuration. It is designed to work with
 * JOOQ's {@link DAO} (Data Access Object) API.
 *
 * @param <RECORD> The type of the JOOQ {@link TableRecord} representing the database table.
 * @param <POJO>   The type of the Plain Old Java Object (POJO) associated with the record.
 * @param <ID>     The type of the identifier or primary key of the entity.
 * @param <REPO>   The type of the JOOQ {@link DAO} repository.
 */
public interface JooqRepositoryFactory<RECORD extends TableRecord<RECORD>, POJO, ID, REPO extends DAO<RECORD, POJO, ID>> {

    /**
     * Creates a new instance of a repository configured with the provided JOOQ configuration.
     * This method abstracts the construction of repository objects and associates them with
     * a specific {@link Configuration} for database interactions.
     *
     * @param configuration the JOOQ configuration used to set up the repository
     * @return the newly created repository instance
     */
    REPO createRepository(Configuration configuration);
}
