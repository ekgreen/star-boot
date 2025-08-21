package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * The Id annotation is a specialized form of the DColumn annotation,
 * primarily used to identify fields that represent a unique identifier
 * in a database table. By default, the attribute name is set to "id",
 * and the field is required.
 *
 * Attributes:
 * - `attribute` specifies the name of the database column this field maps to.
 * Defaults to "id".
 * - `required` indicates whether the field is mandatory. Defaults to true.
 *
 * This annotation provides semantic meaning to fields considered as identifiers
 * while retaining the functionality of the DColumn annotation.
 */
@DictionaryColumn
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    @AliasFor(annotation = DictionaryColumn.class)
    String attribute() default "id";

    @AliasFor(annotation = DictionaryColumn.class)
    boolean required() default true;
}
