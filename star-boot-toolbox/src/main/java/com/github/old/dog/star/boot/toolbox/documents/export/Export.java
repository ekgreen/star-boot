package com.github.old.dog.star.boot.toolbox.documents.export;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Export export = (Export) object;
        return Arrays.equals(data, export.data)
               && Objects.equals(exporter, export.exporter)
               && Objects.equals(mimeType, export.mimeType)
               && Objects.equals(extension, export.extension)
               && Objects.equals(documentType, export.documentType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(documentType);
        result = 31 * result + Objects.hashCode(exporter);
        result = 31 * result + Objects.hashCode(mimeType);
        result = 31 * result + Objects.hashCode(extension);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public @NotNull String toString() {
        return "Export{"
               + "documentType='" + documentType + '\''
               + ", exporter='" + exporter + '\''
               + ", mimeType='" + mimeType + '\''
               + ", extension='" + extension + '\''
               + '}';
    }
}
