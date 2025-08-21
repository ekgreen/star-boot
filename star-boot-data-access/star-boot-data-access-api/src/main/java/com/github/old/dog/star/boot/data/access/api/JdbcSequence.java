package com.github.old.dog.star.boot.data.access.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a JDBC sequence key for use with database sequences.
 * This annotation can be applied to classes to specify a unique database sequence
 * key associated with the class, typically used for generating unique identifiers.
 * <p>
 * The specified sequence key is critical in configurations where database-backed ID
 * generation is required. It is expected to match a sequence configuration in the
 * database or in the corresponding application logic (e.g., DAO or DSL operations).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcSequence {

    /**
     * Specifies the unique sequence key associated with a database sequence.
     * The key is used to identify the sequence for operations such as
     * auto-generating unique identifiers.
     *
     * @return the unique sequence key as a String
     */
    String key();
}
