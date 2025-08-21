package com.github.old.dog.star.boot.dictionaries.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The DColumn annotation is used to map a field or annotation to a specific
 * database column. It provides metadata about the column name and indicates
 * whether the column is mandatory.
 * <p>
 * Attributes:
 * - `attribute` specifies the name of the database column this field or annotation
 * maps to. Defaults to an empty string.
 * - `required` indicates whether the field or annotation is mandatory. Defaults to false.
 * <p>
 * This annotation can be applied directly to fields or to other annotations, allowing
 * for extensibility and reuse in domain-specific annotations.
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DictionaryColumn {

    /**
     * Specifies the name of the database column that the annotated field or annotation maps to.
     * By default, it is set to an empty string, indicating the attribute name is not explicitly provided.
     *
     * @return the name of the database column, defaulting to an empty string
     */
    String attribute() default "";

    /**
     * Indicates whether the annotated field or annotation is mandatory.
     *
     * @return true if the field or annotation is mandatory; false otherwise. Defaults to false.
     */
    boolean required() default false;
}
