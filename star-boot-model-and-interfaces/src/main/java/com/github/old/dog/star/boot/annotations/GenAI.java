package com.github.old.dog.star.boot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The GenAI annotation is used to specify metadata for a generative AI model.
 * It provides details about the model name, its version, and the underwriter or entity responsible.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenAI {
    /**
     * Retrieves the name of the generative AI model.
     *
     * @return the model name as a string
     */
    String model();

    /**
     * Retrieves the version of the generative AI model.
     *
     * @return the model version as a string
     */
    String version();

    /**
     * Retrieves the name of the underwriter or entity responsible for the generative AI model.
     *
     * @return the underwriter's name as a string
     */
    String underwriter();
}
