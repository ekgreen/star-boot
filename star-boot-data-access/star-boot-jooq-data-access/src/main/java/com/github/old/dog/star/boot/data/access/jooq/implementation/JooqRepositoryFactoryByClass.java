package com.github.old.dog.star.boot.data.access.jooq.implementation;

import com.github.old.dog.star.boot.data.access.jooq.api.factories.JooqRepositoryFactory;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import lombok.RequiredArgsConstructor;
import org.jooq.Configuration;
import org.jooq.DAO;
import org.jooq.TableRecord;

/**
 * A factory implementation for creating JOOQ repository instances using reflection. This
 * class facilitates the dynamic instantiation of repository implementations by specifying
 * the repository class type and a JOOQ {@link Configuration} object at runtime.
 *
 * @param <RECORD> The type of the JOOQ {@link TableRecord} representing the database table.
 * @param <POJO>   The type of the Plain Old Java Object (POJO) associated with the record.
 * @param <ID>     The type of the identifier or primary key of the entity.
 * @param <REPO>   The type of the JOOQ {@link DAO} repository.
 */
@RequiredArgsConstructor
public class JooqRepositoryFactoryByClass<RECORD extends TableRecord<RECORD>, POJO, ID, REPO extends DAO<RECORD, POJO, ID>>
        implements JooqRepositoryFactory<RECORD, POJO, ID, REPO> {

    private final Class<REPO> repositoryClassType;

    @Override
    public REPO createRepository(Configuration configuration) {
        return ReflectionTools.newInstance(repositoryClassType, new Class[]{Configuration.class}, new Object[]{configuration});
    }
}
