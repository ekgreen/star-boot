package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The DictionaryTable annotation is used to associate a class with a specific database table
 * within a schema. It provides metadata about the database schema and table name
 * to which the class is mapped.
 * <p>
 * Attributes:
 * - `schema`: Specifies the name of the schema in which the table resides.
 * - `table`: Specifies the name of the database table.
 * <p>
 * This annotation is typically applied to classes representing entities in a
 * relational database, enabling easy identification of the schema and table
 * associated with the entity.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DictionaryTable {
    /**
     * Specifies the name of the schema in which the database table resides.
     *
     * @return the name of the schema
     */
    String schema();

    /**
     * Specifies the name of the database table associated with the annotated class.
     *
     * @return the name of the database table
     */
    String table();
}
