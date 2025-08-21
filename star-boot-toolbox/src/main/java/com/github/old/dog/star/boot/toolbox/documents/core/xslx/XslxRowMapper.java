package com.github.old.dog.star.boot.toolbox.documents.core.xslx;

import com.github.old.dog.star.boot.interfaces.Converter;
import org.apache.poi.ss.usermodel.Row;

/**
 * The XslxRowMapper interface defines a contract for mapping individual rows
 * of an Excel spreadsheet into domain-specific objects of type R and validating
 * the header row of the spreadsheet.
 * <p>
 * This interface extends the Converter interface, allowing the implementation
 * to provide custom logic for converting Excel rows into objects, with an additional
 * responsibility to ensure that the headers in the Excel file conform to the expected format.
 * This header validation is typically used to verify the structure of the Excel file before
 * processing the content rows.
 *
 * @param <R> the type of the object that each row of the Excel spreadsheet will be mapped to
 */
public interface XslxRowMapper<R> extends Converter<Row, R> {

    /**
     * Validates the headers of the provided Excel row to ensure they conform
     * to the expected format. This method is typically used to verify that
     * the structure of the spreadsheet is correct before processing its content.
     *
     * @param headerRow the header row of the Excel sheet to validate
     * @return true if the headers are valid and comply with the expected format;
     * false otherwise
     */
    boolean validateHeaders(Row headerRow);
}
