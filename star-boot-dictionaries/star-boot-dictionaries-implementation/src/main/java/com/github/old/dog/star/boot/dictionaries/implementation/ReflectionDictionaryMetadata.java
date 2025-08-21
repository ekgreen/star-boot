package com.github.old.dog.star.boot.dictionaries.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotatedElementUtils;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryColumn;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryTable;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAccessException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAttributeNotFoundException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryInstantiationException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryMetadataException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryTypeConversionException;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import com.github.old.dog.star.boot.toolbox.strings.Strings;

/**
 * Реализация {@link DictionaryMetadata} на основе Java Reflection API.
 *
 * <p>Использует reflection для анализа структуры классов и работы с атрибутами.
 * Обеспечивает кэширование метаданных для повышения производительности.</p>
 *
 * <h3>Особенности реализации:</h3>
 * <ul>
 *   <li>Автоматическое определение имени таблицы из JPA аннотаций</li>
 *   <li>Поддержка полей любого уровня доступности</li>
 *   <li>Кэширование результатов анализа полей</li>
 *   <li>Безопасное преобразование типов</li>
 * </ul>
 *
 * @implNote Данная реализация потокобезопасна для операций чтения после инициализации
 */
@Slf4j
public final class ReflectionDictionaryMetadata implements DictionaryMetadata {

    private final Class<?> dictionaryClass;
    private final String tableName;
    private final Map<String, Attribute> attributeCache;
    private final Set<String> attributeNames;

    /**
     * Создает метаданные для указанного класса.
     *
     * @param dictionaryClass класс словарного объекта
     * @throws IllegalArgumentException если класс равен null
     */
    public ReflectionDictionaryMetadata(Class<?> dictionaryClass) {
        this.dictionaryClass = Objects.requireNonNull(dictionaryClass, "Dictionary class cannot be null");
        this.tableName = this.buildTableName(dictionaryClass);
        this.attributeCache = this.buildAttributeCache(dictionaryClass);
        this.attributeNames = Set.copyOf(attributeCache.keySet());
    }

    @Override
    public Class<?> getDictionaryClass() {
        return dictionaryClass;
    }

    @Override
    public String getDictionaryTable() {
        return tableName;
    }

    @Override
    public Set<String> getDictionaryAttributes() {
        return attributeNames;
    }

    @Override
    public <T> T newInstance() throws DictionaryInstantiationException {
        try {
            // noinspection unchecked
            return (T) ReflectionTools.newInstance(dictionaryClass);
        } catch (Exception e) {
            throw new DictionaryInstantiationException(
                "Не удалось создать экземпляр класса " + dictionaryClass.getName(), e);
        }
    }

    @Override
    public void set(String attributeName, Object subject, Object value)
        throws DictionaryAttributeNotFoundException, DictionaryTypeConversionException {

        Objects.requireNonNull(attributeName, "Attribute name cannot be null");
        Objects.requireNonNull(subject, "Subject cannot be null");

        Attribute attribute = this.getAttributeByName(attributeName);

        try {
            Object convertedValue = convertValue(value, attribute.getType());
            attribute.set(subject, convertedValue);
        } catch (Exception e) {
            throw new DictionaryTypeConversionException("Ошибка преобразования типа для атрибута: " + attributeName, e);
        }
    }

    @Override
    public Object get(String attributeName, Object subject)
        throws DictionaryAttributeNotFoundException, DictionaryAccessException {

        Objects.requireNonNull(attributeName, "Attribute name cannot be null");
        Objects.requireNonNull(subject, "Subject cannot be null");

        Attribute attribute = getAttributeByName(attributeName);

        try {
            return attribute.get(subject);
        } catch (Exception ex) {
            throw new DictionaryAccessException("Нет доступа к атрибуту: " + attributeName, ex);
        }
    }

    @Override
    public boolean hasAttribute(String attributeName) {
        return attributeName != null && attributeCache.containsKey(attributeName);
    }

    @Override
    public Class<?> getAttributeType(String attributeName) throws DictionaryAttributeNotFoundException {
        Attribute attribute = getAttributeByName(attributeName);
        return attribute.getType();
    }


    // ============================================================================================================== //

    private @NotNull Attribute getAttributeByName(String attributeName) throws DictionaryAttributeNotFoundException {
        Attribute attribute = attributeCache.get(attributeName);
        if (attribute == null) {
            throw new DictionaryAttributeNotFoundException(attributeName);
        }
        return attribute;
    }

    /**
     * Преобразует значение к нужному типу
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (ReflectionTools.isAssignable(value.getClass(), targetType)) {
            return value;
        }

        // Базовые преобразования типов
        if (targetType == String.class) {
            return value.toString();
        }

        return switch (value) {
            case Timestamp timestamp -> convertFromTimestamp(timestamp, targetType);
            case String str -> convertFromString(str, targetType);
            case Number number -> convertFromNumber(number, targetType);
            default -> value;
        };
    }

    // ============================================================================================================== //

    private Object convertFromString(String str, Class<?> targetType) {
        try {
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(str);
            }
            if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(str);
            }
            if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(str);
            }
            if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(str);
            }
            // Добавить другие типы по необходимости
        } catch (NumberFormatException e) {
            throw new DictionaryTypeConversionException("Cannot convert '" + str + "' to " + targetType.getSimpleName(), e);
        }

        return str;
    }

    private Object convertFromNumber(Number number, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) {
            return number.intValue();
        }
        if (targetType == long.class || targetType == Long.class) {
            return number.longValue();
        }
        if (targetType == double.class || targetType == Double.class) {
            return number.doubleValue();
        }
        if (targetType == float.class || targetType == Float.class) {
            return number.floatValue();
        }

        return number;
    }

    /**
     * Конвертирует Timestamp в указанный тип
     */
    public Object convertFromTimestamp(Timestamp timestamp, Class<?> targetType) {
        if (timestamp == null) {
            return null;
        }

        if (targetType == Timestamp.class) {
            return timestamp;
        }

        if (targetType == LocalDateTime.class) {
            return timestamp.toLocalDateTime();
        }

        if (targetType == LocalDate.class) {
            return timestamp.toLocalDateTime().toLocalDate();
        }

        if (targetType == LocalTime.class) {
            return timestamp.toLocalDateTime().toLocalTime();
        }

        if (targetType == Instant.class) {
            return timestamp.toInstant();
        }

        if (targetType == Date.class) {
            return new Date(timestamp.getTime());
        }

        if (targetType == Long.class || targetType == long.class) {
            return timestamp.getTime();
        }

        if (targetType == String.class) {
            return timestamp.toString();
        }

        throw new IllegalArgumentException(
            String.format("Не поддерживается конвертация Timestamp в %s", targetType.getName())
        );
    }

    // ============================================================================================================== //

    /**
     * Извлекает имя таблицы из аннотаций класса
     */
    private String buildTableName(Class<?> clazz) {
        final Class<DictionaryTable> dictionaryTableClass = DictionaryTable.class;

        if (!clazz.isAnnotationPresent(dictionaryTableClass)) {
            log.warn("Сущность {} не имеет аннотации {}. Любая справочная сущность должна "
                     + "иметь аннотацию {}", clazz.getName(), dictionaryTableClass.getName(), dictionaryTableClass.getName());

            throw new DictionaryMetadataException("dictionary entity mast be annotated with " + dictionaryTableClass.getName());
        }

        final DictionaryTable annotation = clazz.getDeclaredAnnotation(dictionaryTableClass);

        return String.format("%s.%s", annotation.schema(), annotation.table());
    }

    /**
     * Строит кэш полей класса
     */
    private Map<String, Attribute> buildAttributeCache(Class<?> clazz) {
        Map<String, Attribute> cache = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            // Пропускаем static и transient поля
            // noinspection PointlessBooleanExpression,DuplicateCondition
            if (false
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())
            ) {
                continue;
            }

            // noinspection PointlessBooleanExpression,DuplicateCondition
            if (false
                || ReflectionTools.isPrimal(field.getType())
                || ReflectionTools.isTime(field.getType())
            ) {
                Attribute attribute = buildAttribute(field);
                cache.putIfAbsent(attribute.name, attribute);
            }
        }

        return Map.copyOf(cache);
    }

    private Attribute buildAttribute(Field field) {
        // 1. Проверяем специальные аннотации через AliasFor механизм
        final DictionaryColumn dColumn = AnnotatedElementUtils.getMergedAnnotation(field, DictionaryColumn.class);

        if (Objects.nonNull(dColumn)) {
            return makeAttribute(dColumn, field);
        }

        // 2. Генерируем из имени поля с предупреждением
        log.warn("Поле '{}' в классе '{}' не имеет специальных аннотаций для определения имени атрибута. "
                 + "Используется автогенерированное имя на основе имени поля.",
            field.getName(), field.getDeclaringClass().getSimpleName());

        String snakeName = Strings.changeCase(Strings.TextCase.CAMEL_CASE, Strings.TextCase.SNAKE_CASE, field.getName());

        return makeAttribute(snakeName, field);
    }


    private Attribute makeAttribute(DictionaryColumn dictionaryColumn, Field field) {
        return Attribute.builder()
            .name(dictionaryColumn.attribute())
            .required(dictionaryColumn.required())
            .field(field)
            .build();
    }

    private Attribute makeAttribute(String name, Field field) {
        return Attribute.builder()
            .name(name)
            .required(false)
            .field(field)
            .build();
    }

    // ============================================================================================================== //


    @RequiredArgsConstructor
    @Builder
    private static class Attribute {
        private final String name;
        private final boolean required;
        private final Field field;

        public Object get(Object subject) {
            return ReflectionTools.readValue(field, subject);
        }

        public Attribute set(Object subject, Object value) {
            ReflectionTools.setValue(field, subject, value);
            return this;
        }

        public Class<?> getType() {
            return field.getType();
        }
    }
}
