package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * The Name annotation is a specialized form of the DColumn annotation,
 * specifically designed to mark fields that represent a "name" attribute
 * within a database table. By default, the attribute name is set to "name",
 * and the field is not required.
 *
 * Attributes:
 * - `attribute` specifies the name of the database column this field maps to.
 * Defaults to "name".
 * - `required` indicates whether the field is mandatory. Defaults to false.
 *
 * This annotation provides semantic meaning for fields that are treated as
 * "name" attributes while extending the functionality of the DColumn annotation.
 */
@DictionaryColumn
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    @AliasFor(annotation = DictionaryColumn.class)
    String attribute() default "name";

    @AliasFor(annotation = DictionaryColumn.class)
    boolean required() default false;

}
