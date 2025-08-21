package com.github.old.dog.star.boot.toolbox.documents.export;

import org.jetbrains.annotations.NotNull;

/**
 * Defines a contract for exporting data in various formats.
 * <p>
 * Implementations of this interface are responsible for exporting objects
 * to a specific format based on the provided {@link ExporterType}.
 * <p>
 * The export method accepts a type and an object to export, and returns
 * an {@link Export} object containing the exported data.
 */
public interface Exporter {

    /**
     * Exports the given object in the format specified by the {@link ExporterType}.
     * <p>
     * Implementations of this method are responsible for converting the provided object
     * into an {@link Export} representing the exported data, including metadata such as
     * MIME type, file extension, and binary content.
     *
     * @param type      the format in which to export the data, represented by {@link ExporterType};
     *                  must not be null
     * @param forExport the object or data to be exported; must not be null
     * @return an {@link Export} containing the exported data and associated metadata
     * @throws IllegalArgumentException if the specified {@link ExporterType} is not supported
     */
    Export export(@NotNull ExporterType type, @NotNull Object forExport);
}
