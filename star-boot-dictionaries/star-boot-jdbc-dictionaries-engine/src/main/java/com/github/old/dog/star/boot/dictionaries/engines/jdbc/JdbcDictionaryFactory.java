package com.github.old.dog.star.boot.dictionaries.engines.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import com.github.old.dog.star.boot.dictionaries.api.Dictionary;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryEngine;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryFactory;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryFactoryException;
import com.github.old.dog.star.boot.dictionaries.implementation.DictionaryBridge;
import com.github.old.dog.star.boot.dictionaries.implementation.ReflectionDictionaryMetadata;

/**
 * JDBC реализация фабрики справочников.
 *
 * <p>Создает экземпляры {@code RawDictionary} с использованием:
 * <ul>
 *   <li>{@code ReflectionDictionaryMetadata} для работы с метаданными классов</li>
 *   <li>{@code JdbcTemplateDictionaryEngine} для доступа к данным через JDBC</li>
 * </ul>
 *
 * <p>Фабрика является потокобезопасной и может использоваться как singleton bean.</p>
 *
 * <h3>Требования к словарным классам:</h3>
 * <ul>
 *   <li>Публичный класс (не интерфейс, не абстрактный)</li>
 *   <li>Конструктор по умолчанию</li>
 *   <li>Поля с геттерами/сеттерами или прямым доступом</li>
 * </ul>
 *
 * @author AI Assistant
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class JdbcDictionaryFactory implements DictionaryFactory {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public <T> Dictionary<T> createDictionary(Class<T> dictionaryClass, String tableName) {
        validateParameters(dictionaryClass, tableName);

        log.debug("Создание JDBC словаря для класса {} с таблицей {}",
            dictionaryClass.getSimpleName(), tableName);

        try {
            // Проверяем поддержку класса
            if (!supports(dictionaryClass)) {
                throw new DictionaryFactoryException(
                    String.format("Класс %s не поддерживается JDBC фабрикой", dictionaryClass.getName()));
            }

            // Создаем метаданные
            DictionaryMetadata metadata = createMetadata(dictionaryClass);
            log.debug("Созданы метаданные для класса {}: атрибутов {}",
                dictionaryClass.getSimpleName(), metadata.getDictionaryAttributes().size());

            // Создаем движок
            DictionaryEngine engine = createEngine(tableName);
            log.debug("Создан JDBC движок для таблицы {}", tableName);

            // Создаем справочник
            Dictionary<T> dictionary = new DictionaryBridge<>(metadata, engine);

            log.info("Успешно создан JDBC словарь: класс={}, таблица={}, атрибутов={}",
                dictionaryClass.getSimpleName(), tableName, metadata.getDictionaryAttributes().size());

            return dictionary;

        } catch (Exception e) {
            String errorMessage = String.format("Не удалось создать JDBC словарь для класса %s (таблица: %s): %s",
                dictionaryClass.getSimpleName(), tableName, e.getMessage());
            log.error(errorMessage, e);
            throw new DictionaryFactoryException(errorMessage, e);
        }
    }

    @Override
    public String getFactoryName() {
        return "JDBC Dictionary Factory";
    }

    /**
     * Создает метаданные для указанного класса.
     */
    private <T> DictionaryMetadata createMetadata(Class<T> dictionaryClass) {
        try {
            return new ReflectionDictionaryMetadata(dictionaryClass);
        } catch (Exception e) {
            throw new DictionaryFactoryException(
                String.format("Не удалось создать метаданные для класса %s", dictionaryClass.getName()), e);
        }
    }

    /**
     * Создает движок для указанной таблицы.
     */
    private DictionaryEngine createEngine(String tableName) {
        try {
            return new JdbcTemplateDictionaryEngine(tableName, jdbcTemplate);
        } catch (Exception e) {
            throw new DictionaryFactoryException(
                String.format("Не удалось создать JDBC движок для таблицы %s", tableName), e);
        }
    }

    /**
     * Валидирует входные параметры.
     */
    private void validateParameters(Class<?> dictionaryClass, String tableName) {
        if (dictionaryClass == null) {
            throw new IllegalArgumentException("Класс словаря не может быть null");
        }

        if (!StringUtils.hasText(tableName)) {
            throw new IllegalArgumentException("Имя таблицы не может быть null или пустым");
        }

        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя таблицы не может содержать только пробелы");
        }
    }
}
