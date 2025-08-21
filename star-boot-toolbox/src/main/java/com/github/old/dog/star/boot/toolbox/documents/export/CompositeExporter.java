package com.github.old.dog.star.boot.toolbox.documents.export;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * A composite implementation of the {@link Exporter} interface that delegates export operations
 * to specific {@link ExporterProcessor} implementations based on the provided {@link ExporterType}.
 * <p>
 * This class manages a collection of {@link ExporterProcessor} instances and routes export requests
 * to the appropriate processor by matching the specified {@link ExporterType}.
 */
public class CompositeExporter implements Exporter {

    private final Map<ExporterType, ExporterProcessor> processors;

    public CompositeExporter(List<ExporterProcessor> processors) {
        this.processors = processors.stream().collect(Collectors.toMap(
            ExporterProcessor::getType,
            Function.identity()
        ));
    }

    @Override
    public Export export(@NotNull ExporterType type, @NotNull Object forExport) {
        final ExporterProcessor processor = processors.get(type);

        if (Objects.isNull(processor)) {
            throw new IllegalArgumentException("unknow `ExporterType`: " + type);
        }

        return processor.export(forExport);
    }
}
