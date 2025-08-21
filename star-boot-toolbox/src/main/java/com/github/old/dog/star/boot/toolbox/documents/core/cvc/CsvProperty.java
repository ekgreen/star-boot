package com.github.old.dog.star.boot.toolbox.documents.core.cvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CsvProperty is a custom annotation used to map fields or annotations to
 * specific CSV headers during the CSV export process. It is applied to
 * fields or annotation types to define the corresponding header name in
 * the generated CSV file.
 *
 * This annotation is runtime-retained and can be used on fields or other
 * annotations.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvProperty {

    /**
     * Specifies the name of the CSV header that a field or annotation type should be mapped to.
     * This value is used during the CSV export process to define the corresponding column name
     * in the generated CSV file.
     *
     * @return the name of the CSV header to which the field or annotation type is mapped
     */
    String header();
}
