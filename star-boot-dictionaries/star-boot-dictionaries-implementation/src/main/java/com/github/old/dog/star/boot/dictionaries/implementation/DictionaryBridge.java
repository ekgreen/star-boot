package com.github.old.dog.star.boot.dictionaries.implementation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import com.github.old.dog.star.boot.dictionaries.api.Dictionary;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryEngine;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryRow;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The DictionaryBridge class serves as a bridge between a dictionary's metadata and its engine,
 * providing functionalities for managing and accessing dictionary records. This class implements
 * the Dictionary interface, defining methods to retrieve dictionary metadata and perform CRUD
 * operations on its records. The generic type parameter T represents the type of records stored
 * in the dictionary.
 * <p>
 * Features:
 * - Provides dictionary type and metadata information such as the dictionary name and attributes.
 * - Allows retrieving all dictionary records, or specific records by ID, code, or name.
 * - Supports mapping raw dictionary rows from the engine into strongly-typed objects of type T.
 * - Logs diagnostic information about operations including data loading, mapping, and errors.
 * <p>
 * Thread Safety:
 * - This class is thread-safe for concurrent reads but thread safety for writes depends
 * on the underlying DictionaryEngine and DictionaryMetadata implementation.
 * <p>
 * Exception Handling:
 * - Throws DictionaryException for critical errors during mapping or data retrieval operations.
 *
 * @param <T> The type of records stored in the dictionary
 */
@Slf4j
@RequiredArgsConstructor
public class DictionaryBridge<T> implements Dictionary<T> {

    private static final String OPERATION_GET_ALL = "getAll";
    private static final String OPERATION_GET_BY_ID = "getById";
    private static final String OPERATION_GET_BY_CODE = "getByCode";
    private static final String OPERATION_GET_BY_NAME = "getByName";

    private final DictionaryMetadata metadata;
    private final DictionaryEngine engine;

    @Override
    public Class<T> getDictionaryType() {
        // noinspection unchecked
        return (Class<T>) metadata.getDictionaryClass();
    }

    @Override
    public String getDictionaryName() {
        return metadata.getDictionaryTable();
    }

    @Override
    public Set<String> getAttributesName() {
        return metadata.getDictionaryAttributes();
    }

    @Override
    public List<T> getAll() {
        return this.findMany(DictionaryBridge.OPERATION_GET_ALL, engine::getAll);
    }

    @Override
    public Optional<T> getById(int id) {
        return this.findOne(DictionaryBridge.OPERATION_GET_BY_ID, () -> engine.getById(id));
    }

    @Override
    public Optional<T> getByCode(String code) {
        return this.findOne(DictionaryBridge.OPERATION_GET_BY_CODE, () -> engine.getByCode(code));
    }

    @Override
    public Optional<T> getByName(String name) {
        return this.findOne(DictionaryBridge.OPERATION_GET_BY_NAME, () -> engine.getByName(name));
    }

    public List<T> findMany(String operation, Supplier<List<DictionaryRow>> accessor) {
        String dictionaryName = getDictionaryName();
        Class<?> dictionaryClass = metadata.getDictionaryClass();

        log.info("Начало загрузки {} справочника '{}' для типа {}",
            operation, dictionaryName, dictionaryClass.getSimpleName());

        try {
            List<DictionaryRow> rawRows = accessor.get();

            if (log.isDebugEnabled()) {
                log.debug("Получено {} не преобразованных строк справочника '{} из движка {}'",
                    rawRows.size(), dictionaryName, engine.getClass().getSimpleName());
            }

            if (rawRows.isEmpty()) {
                log.warn("Справочник '{}' не содержит записей", dictionaryName);
                return List.of();
            }

            List<T> mappedObjects = rawRows.stream()
                .map(this::mapRow)
                .toList();

            log.info("Успешно загружены и преобразованы записи справочника '{}': количество объектов: {}",
                dictionaryName, mappedObjects.size());

            // Дополнительная валидация
            if (mappedObjects.size() != rawRows.size()) {
                log.warn("Количество преобразованных объектов ({}) не совпадает с количеством исходных записей ({}) для справочника '{}'",
                    mappedObjects.size(), rawRows.size(), dictionaryName);
            }

            return mappedObjects;

        } catch (RuntimeException e) {
            log.error("Ошибка при загрузке записей справочника '{}' для типа {}: {}",
                dictionaryName, dictionaryClass.getSimpleName(), e.getMessage(), e);
            throw new DictionaryException("Не удалось загрузить записи справочника", e);
        }
    }

    public Optional<T> findOne(String operation, Supplier<Optional<DictionaryRow>> accessor) {
        final List<T> many = this.findMany(operation, () -> accessor.get().map(List::of).orElse(List.of()));
        return many.isEmpty() ? Optional.empty() : Optional.of(many.getFirst());
    }

    private T mapRow(DictionaryRow dictionaryRow) {
        try {
            // noinspection unchecked
            final T dictionary = (T) metadata.newInstance();

            int mappedAttributes = 0;
            int skippedAttributes = 0;

            for (String attributeName : dictionaryRow.getAttributeNames()) {
                if (metadata.hasAttribute(attributeName)) {
                    try {
                        Object value = dictionaryRow.attribute(attributeName);
                        metadata.set(attributeName, dictionary, value);
                        mappedAttributes++;

                        if (log.isTraceEnabled()) {
                            log.trace("Установлен атрибут '{}' = '{}' для записи справочника '{}'",
                                attributeName, value, getDictionaryName());
                        }

                    } catch (Exception e) {
                        log.warn("Ошибка при установке атрибута '{}' для записи справочника '{}': {}",
                            attributeName, getDictionaryName(), e.getMessage());
                        // Продолжаем обработку других атрибутов
                    }
                } else {
                    skippedAttributes++;

                    if (log.isTraceEnabled()) {
                        log.trace("Пропущен неизвестный атрибут '{}' для записи справочника '{}'",
                            attributeName, getDictionaryName());
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Преобразование записи справочника '{}' завершено: установлено атрибутов: {}, пропущено: {}",
                    getDictionaryName(), mappedAttributes, skippedAttributes);
            }

            return dictionary;

        } catch (Exception e) {
            log.error("Критическая ошибка при преобразовании записи справочника '{}': {}",
                getDictionaryName(), e.getMessage(), e);
            throw new DictionaryException("Не удалось преобразовать запись справочника: " + e.getMessage(), e);
        }
    }
}
