package com.github.old.dog.star.boot.toolbox.documents.core.xslx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The Xslx class provides utility methods for converting Excel data represented as
 * byte arrays into a list of domain-specific objects. It processes the Excel data using
 * Apache POI libraries, ensuring header validation and row-by-row conversion. The class
 * is designed to be used with a custom implementation of the XslxRowMapper interface,
 * which defines the mapping logic and header validation.
 * <p>
 * This class is final and cannot be instantiated.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Xslx {

    /**
     * Converts an Excel file represented as a byte array into a list of objects of type R.
     * This method processes the Excel file using the provided {@link XslxRowMapper}, which handles
     * header validation and row conversion logic. It ensures that the structure of the Excel file
     * is valid before processing its data rows.
     *
     * @param <R>       the type of objects contained in the resulting list
     * @param excelData the byte array representation of the Excel file to be processed
     * @param rowMapper the mapper responsible for validating the headers and mapping each row
     *                  of the Excel sheet into objects of type R
     * @return a list of objects of type R mapped from the rows of the Excel file
     * @throws ExcelParsingException if the structure of the Excel file is invalid,
     *                               its headers do not conform to the expected format,
     *                               or the file cannot be read or processed
     */
    public static <R> List<R> convert(byte[] excelData, XslxRowMapper<R> rowMapper) {
        try (
            ByteArrayInputStream bis = new ByteArrayInputStream(excelData);
            Workbook workbook = new XSSFWorkbook(bis)
        ) {
            final Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист
            final List<R> result = new ArrayList<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    // валидация заголовков

                    if (!rowMapper.validateHeaders(row)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Структура заголовков Excel файла не соответствует ожидаемой");
                        }

                        throw new ExcelParsingException(
                            "Некорректная структура Excel файла: несоответствие заголовков ожидаемому формату. Пожалуйста, проверьте, "
                            + "что все необходимые колонки присутствуют и имеют правильные названия");
                    }
                    continue;
                } else {
                    // обработка тела

                    try {
                        R object = rowMapper.convert(row);
                        result.add(object);
                    } catch (Exception e) {
                        log.warn("Error parsing row {}: {}", row.getRowNum(), e.getMessage());
                    }
                }

            }

            return result;
        } catch (IOException ioException) {
            log.error("Ошибка при чтении Excel файла: {}", ioException.getMessage());
            log.error("Детали ошибки:", ioException);

            throw new ExcelParsingException("Не удалось прочитать Excel файл. Проверьте, что файл не поврежден и доступен для чтения. "
                                            + "Детали ошибки: " + ioException.getMessage(), ioException);

        }
    }
}
