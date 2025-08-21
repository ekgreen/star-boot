package com.github.old.dog.star.boot.dictionaries.api.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Code annotation is a specialized form of the DColumn annotation,
 * specifically used for marking fields that represent a "code" attribute
 * in dictionary tables. By default, the attribute name is set to "code"
 * and the field is not required.
 * <p>
 * This annotation inherits from the DColumn annotation and retains its
 * functionality, while adding semantic meaning for fields that are
 * considered "code" attributes.
 * <p>
 * Attributes:
 * - `attribute` specifies the name of the database column this field maps to.
 * Defaults to "code".
 * - `required` indicates whether the field is mandatory. Defaults to false.
 */
@DictionaryColumn
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Code {

    /**
     * Specifies the name of the database column this field maps to. This attribute
     * acts as an alias for the `attribute` property in the {@link DictionaryColumn} annotation.
     *
     * @return the name of the database column, defaulting to "code"
     */
    @AliasFor(annotation = DictionaryColumn.class)
    String attribute() default "code";

    /**
     * Indicates whether the annotated field is mandatory. This attribute acts as an alias
     * for the `required` property in the {@link DictionaryColumn} annotation.
     *
     * @return true if the annotated field is mandatory; false otherwise. Defaults to false.
     */
    @AliasFor(annotation = DictionaryColumn.class)
    boolean required() default false;

}
