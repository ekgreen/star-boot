package com.github.old.dog.star.boot.toolbox.documents.export;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the supported types of exporters for exporting data to various formats.
 *
 * This enum provides the following export types:
 * - CSV: Representing Comma-Separated Values format.
 * - JSON: Representing JavaScript Object Notation format.
 *
 * Each type can return its lowercase name as a string using the {@link #getValue()} method.
 * This allows for consistent representation of the export type when serialized or used in other parts of the system.
 */
public enum ExporterType {
    CSV, JSON;

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
