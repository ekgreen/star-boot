package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * The ParentId annotation is a specialized form of the DColumn annotation, designed
 * to associate a field with a "parent_id" attribute in a database table. By default,
 * the database column name is set to "parent_id" and the field is not required.
 *
 * This annotation extends the functionality provided by the DColumn annotation and
 * applies semantic meaning for fields that represent parent-child relationships in
 * relational database structures.
 *
 * Attributes:
 * - `attribute`: Defines the name of the database column this field maps to. Defaults to "parent_id".
 * - `required`: Specifies whether this field is mandatory. Defaults to false.
 */
@DictionaryColumn
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentId {

    @AliasFor(annotation = DictionaryColumn.class)
    String attribute() default "parent_id";

    @AliasFor(annotation = DictionaryColumn.class)
    boolean required() default false;

}
