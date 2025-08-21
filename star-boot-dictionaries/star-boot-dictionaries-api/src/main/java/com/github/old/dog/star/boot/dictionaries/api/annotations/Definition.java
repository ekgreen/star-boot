package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * The Definition annotation is a specialized variant of the DColumn annotation,
 * specifically designed for marking fields that represent a "definition" attribute
 * in dictionary tables. By default, the attribute name in the database is set to "definition",
 * and the field is not required.
 * <p>
 * Attributes:
 * - `attribute` specifies the name of the database column this field maps to. Defaults to "definition".
 * - `required` indicates whether the field is mandatory. Defaults to false.
 * <p>
 * This annotation provides semantic meaning to fields that are associated with "definition" attributes
 * while retaining the functionality of the DColumn annotation.
 */
@DictionaryColumn
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Definition {

    @AliasFor(annotation = DictionaryColumn.class)
    String attribute() default "definition";

    @AliasFor(annotation = DictionaryColumn.class)
    boolean required() default false;

}
