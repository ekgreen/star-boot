package com.github.old.dog.star.boot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a type should be ignored by Checkstyle during static code analysis.
 *
 * This annotation allows developers to mark specific classes or types that do not need
 * to conform to Checkstyle rules. It is typically used in scenarios where the rules
 * are overridden intentionally for specific code sections.
 *
 * The @CheckstyleIgnore annotation is retained only in the source code and has no effect
 * on runtime behavior.
 *
 * Usage of this annotation should be limited and well-documented to ensure code maintainability
 * and clarity.
 *
 * Target: This annotation can only be applied to types (classes, interfaces, etc.).
 * Retention: This annotation is retained in the source code and not included in the compiled bytecode.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.SOURCE)
public @interface CheckstyleIgnore {
}
