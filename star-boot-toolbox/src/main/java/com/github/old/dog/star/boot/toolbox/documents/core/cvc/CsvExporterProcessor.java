package com.github.old.dog.star.boot.toolbox.documents.core.cvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import com.github.old.dog.star.boot.toolbox.core.Flatter;
import com.github.old.dog.star.boot.toolbox.documents.export.Export;
import com.github.old.dog.star.boot.toolbox.documents.export.ExporterProcessor;
import com.github.old.dog.star.boot.toolbox.documents.export.ExporterType;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import com.opencsv.CSVWriter;

/**
 * The CsvExporterProcessor class is an implementation of the {@link ExporterProcessor}
 * designed to export data in CSV format. It leverages reflection to flatten and transform
 * input objects into CSV-compliant structures.
 */
public class CsvExporterProcessor extends ExporterProcessor {

    public CsvExporterProcessor() {
        super(ExporterType.CSV);
    }

    @Override
    public Export export(Object subject) {
        // проверка что объект итерируемый
        if (ReflectionTools.notArray(subject)) {
            throw new IllegalArgumentException("cvc exporter does not supports objects with type: "
                                               + (Objects.nonNull(subject) ? subject.getClass().getSimpleName() : null));
        }

        // создадим отображение csv файла
        final @NotNull CsvView view = makeView(subject);

        return new Export(
                "csv",
                this.getClass().getName(),
                "text/csv; charset=UTF-8",
                "csv",
                view.writeAsBytes()
        );
    }

    private CsvView makeView(Object subject) {
        final Set<String> headers = new LinkedHashSet<>();
        final List<Map<String, Object>> flattens = new ArrayList<>();

        // обработаем все поля класса
        ReflectionTools.forEachArray(subject, (_, object) -> {
            final Map<String, Object> flatten = Flatter.flatten(
                    object,
                    new Flatter.AnnotationKeyExtractor<>(CsvProperty.class, CsvProperty::header));

            headers.addAll(flatten.keySet());
            flattens.add(flatten);
        });

        // преобразуем в равно-размерный список
        final List<String[]> rows = new ArrayList<>();
        for (Map<String, Object> flatten : flattens) {
            String[] row = new String[headers.size()];
            int pointer = 0;

            for (String header : headers) {
                Object value = flatten.get(header);
                row[pointer++] = value != null ? String.valueOf(value) : null;
            }

            rows.add(row);
        }

        return new CsvView(headers, rows);
    }

    @Value
    private static class CsvView {
        Set<String> headers;
        List<String[]> rows;

        public byte[] writeAsBytes() {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                 CSVWriter csvWriter = new CSVWriter(writer)) {

                // Добавляем BOM для UTF-8
                out.write(0xEF);
                out.write(0xBB);
                out.write(0xBF);

                // Записываем заголовки
                csvWriter.writeNext(headers.toArray(String[]::new));

                // Записываем данные
                for (String[] rating : rows) {
                    csvWriter.writeNext(rating);
                }

                csvWriter.flush();
                return out.toByteArray();
            } catch (IOException writeException) {
                throw new RuntimeException("Ошибка при создании CSV файла", writeException);
            }
        }
    }

}
