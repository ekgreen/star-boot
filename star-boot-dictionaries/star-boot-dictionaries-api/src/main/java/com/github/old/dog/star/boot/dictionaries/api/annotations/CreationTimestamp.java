package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * The CreationTimestamp annotation is a specialized form of the DColumn annotation,
 * designed for marking fields that represent the timestamp when a record was created.
 * By default, the attribute name in the database is set to "creation_timestamp",
 * and the field is mandatory.
 *
 * Attributes:
 * - `attribute` specifies the name of the database column this field maps to.
 *   Defaults to "creation_timestamp".
 * - `required` indicates whether the field is mandatory. Defaults to true.
 */
@DictionaryColumn
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreationTimestamp {

    /**
     * Specifies the name of the database column that this field maps to. This attribute
     * acts as an alias for the `attribute` property in the {@link DictionaryColumn} annotation.
     *
     * @return the name of the database column, defaulting to "creation_timestamp"
     */
    @AliasFor(annotation = DictionaryColumn.class)
    String attribute() default "creation_timestamp";

    /**
     * Indicates whether the annotated field is mandatory. This attribute acts as an alias
     * for the `required` property in the {@link DictionaryColumn} annotation.
     *
     * @return true if the annotated field is mandatory; false otherwise. Defaults to true.
     */
    @AliasFor(annotation = DictionaryColumn.class)
    boolean required() default true;

}
