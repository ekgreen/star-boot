package com.github.old.dog.star.boot.dictionaries.api.throwbles;

import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;

/**
 * Исключение, возникающее при ошибке преобразования типов данных в процессе работы с атрибутами.
 *
 * <p>Данное исключение выбрасывается методом {@link DictionaryMetadata#set}, когда
 * переданное значение не может быть корректно преобразовано к типу целевого атрибута.</p>
 *
 * <h3>Типичные сценарии возникновения:</h3>
 * <ul>
 *   <li>Попытка присвоить строку числовому полю (например, "abc" → int)</li>
 *   <li>Преобразование между несовместимыми типами (Date → Integer)</li>
 *   <li>Переполнение при конверсии чисел (Long.MAX_VALUE → byte)</li>
 *   <li>Некорректный формат данных (например, "true/false" → Boolean со строгой валидацией)</li>
 *   <li>Попытка присвоить null значение примитивному типу</li>
 *   <li>Ошибки парсинга дат, времени или других форматированных данных</li>
 * </ul>
 *
 * <h3>Поддерживаемые преобразования:</h3>
 * <ul>
 *   <li>Примитивы ↔ их объектные обертки (int ↔ Integer)</li>
 *   <li>Расширяющие числовые преобразования (int → long, float → double)</li>
 *   <li>Строки → примитивы через стандартные парсеры</li>
 *   <li>Numbers → другие числовые типы</li>
 *   <li>Любой тип → String через toString()</li>
 * </ul>
 *
 * <h3>Пример обработки:</h3>
 * <pre>{@code
 * try {
 *     metadata.set("age", person, "thirty"); // некорректное число
 * } catch (DictionaryTypeConversionException e) {
 *     logger.error("Ошибка преобразования типа: {} → {} для атрибута '{}'",
 *                  e.getSourceType(), e.getTargetType(), e.getAttributeName());
 *     // Возможна попытка альтернативного преобразования или запрос корректного значения
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @see DictionaryMetadata#set(String, Object, Object)
 * @since 1.0
 */
public class DictionaryTypeConversionException extends DictionaryMetadataException {

    private final String attributeName;
    private final Class<?> sourceType;
    private final Class<?> targetType;
    private final Object sourceValue;

    /**
     * Создает исключение с базовым сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   первопричина ошибки преобразования
     */
    public DictionaryTypeConversionException(String message, Throwable cause) {
        super(message, cause);
        this.attributeName = null;
        this.sourceType = null;
        this.targetType = null;
        this.sourceValue = null;
    }

    /**
     * Создает исключение с подробной информацией о преобразовании.
     *
     * @param attributeName имя атрибута, для которого выполнялось преобразование
     * @param sourceValue   исходное значение
     * @param targetType    целевой тип
     * @param cause         первопричина ошибки
     */
    public DictionaryTypeConversionException(String attributeName, Object sourceValue,
                                             Class<?> targetType, Throwable cause) {
        super(String.format("Не удалось преобразовать значение '%s' типа '%s' к типу '%s' для атрибута '%s'",
                sourceValue,
                sourceValue != null ? sourceValue.getClass().getSimpleName() : "null",
                targetType != null ? targetType.getSimpleName() : "unknown",
                attributeName),
            cause);
        this.attributeName = attributeName;
        this.sourceType = sourceValue != null ? sourceValue.getClass() : null;
        this.targetType = targetType;
        this.sourceValue = sourceValue;
    }

    /**
     * Создает исключение с явным указанием типов.
     *
     * @param attributeName имя атрибута
     * @param sourceType    исходный тип
     * @param targetType    целевой тип
     * @param sourceValue   исходное значение
     * @param message       кастомное сообщение
     * @param cause         первопричина ошибки
     */
    public DictionaryTypeConversionException(String attributeName, Class<?> sourceType,
                                             Class<?> targetType, Object sourceValue,
                                             String message, Throwable cause) {
        super(message, cause);
        this.attributeName = attributeName;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.sourceValue = sourceValue;
    }

    /**
     * Возвращает имя атрибута, для которого произошла ошибка преобразования.
     *
     * @return имя атрибута или {@code null}, если не указано
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Возвращает исходный тип данных.
     *
     * @return класс исходного типа или {@code null}, если не определен
     */
    public Class<?> getSourceType() {
        return sourceType;
    }

    /**
     * Возвращает целевой тип данных.
     *
     * @return класс целевого типа или {@code null}, если не определен
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Возвращает исходное значение, которое не удалось преобразовать.
     *
     * @return исходное значение (может быть {@code null})
     */
    public Object getSourceValue() {
        return sourceValue;
    }

    /**
     * Проверяет, является ли ошибка результатом попытки присвоить null примитиву.
     *
     * @return {@code true} если ошибка связана с null → primitive преобразованием
     */
    public boolean isNullToPrimitiveError() {
        return sourceValue == null
               && targetType != null
               && targetType.isPrimitive();
    }

    /**
     * Проверяет, является ли ошибка результатом числового переполнения.
     *
     * @return {@code true} если есть признаки числового переполнения
     */
    public boolean isNumericOverflowError() {
        return getCause() instanceof NumberFormatException
               || (sourceType != null && Number.class.isAssignableFrom(sourceType)
                   && targetType != null && (targetType.isPrimitive() || Number.class.isAssignableFrom(targetType)));
    }

    /**
     * Фабричный метод для создания исключения при null → primitive ошибке.
     *
     * @param attributeName имя атрибута
     * @param primitiveType примитивный тип
     * @return новое исключение
     */
    public static DictionaryTypeConversionException nullToPrimitive(String attributeName, Class<?> primitiveType) {
        return new DictionaryTypeConversionException(
            String.format("Нельзя присвоить null значение примитивному типу '%s' для атрибута '%s'",
                primitiveType.getSimpleName(), attributeName),
            null
        );
    }
}
