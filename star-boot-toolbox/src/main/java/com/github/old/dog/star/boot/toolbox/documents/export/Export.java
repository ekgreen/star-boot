package com.github.old.dog.star.boot.toolbox.documents.export;

/**
 * Represents the result of an export operation.
 * <p>
 * This record encapsulates various properties of an exported document,
 * such as its type, exporter information, MIME type, file extension,
 * and binary data.
 * <p>
 * Each instance of this record provides the following properties:
 * - documentType: The type of document being exported.
 * - exporter: The exporter or processor responsible for creating the export.
 * - mimeType: The MIME type associated with the exported content.
 * - extension: The file extension of the exported document.
 * - data: The binary content of the exported document as a byte array.
 */
public record Export(
    String documentType,
    String exporter,
    String mimeType,
    String extension,
    byte[] data
) {
}
