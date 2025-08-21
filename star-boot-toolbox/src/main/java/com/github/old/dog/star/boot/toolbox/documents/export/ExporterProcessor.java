package com.github.old.dog.star.boot.toolbox.documents.export;

import com.github.old.dog.star.boot.toolbox.documents.core.cvc.CsvExporterProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract class for processing and exporting data to various formats.
 * <p>
 * This class serves as a base for specific exporter processors, such as CSV or JSON,
 * providing a common structure for handling export operations. Each implementation
 * of this class corresponds to a specific {@link ExporterType}.
 * <p>
 * The class encapsulates the type of exporter it is associated with and requires
 * extending classes to provide an implementation for the export operation.
 * <p>
 * Responsibilities:
 * - Defines the export type represented by {@link ExporterType}.
 * - Facilitates the exporting of data by enforcing the implementation of the abstract export method.
 * - Provides a static utility method for obtaining a CSV-specific exporter processor.
 */
@RequiredArgsConstructor
public abstract class ExporterProcessor {

    @Getter
    private final ExporterType type;

    /**
     * Exports the provided object and returns the resulting {@link Export}.
     * <p>
     * This method is responsible for processing the given object and transforming
     * it into an exportable format defined by the implementation. The resulting
     * export contains metadata related to the exported data, including its type,
     * MIME type, file extension, and binary content.
     *
     * @param object the object or data to be exported; must not be null
     * @return an {@link Export} containing the exported data and associated metadata
     */
    public abstract Export export(Object object);

    // ================================================================================================================================== //

    /**
     * Provides an {@link ExporterProcessor} instance configured for CSV export.
     * <p>
     * This method returns an instance of {@link CsvExporterProcessor}, which is a specialized
     * implementation of the {@link ExporterProcessor} designed for exporting data in the CSV format.
     * The processor ensures that data is flattened and transformed to comply with CSV standards.
     *
     * @return an {@link ExporterProcessor} instance for CSV export
     */
    public static ExporterProcessor csvExporter() {
        return new CsvExporterProcessor();
    }

}
