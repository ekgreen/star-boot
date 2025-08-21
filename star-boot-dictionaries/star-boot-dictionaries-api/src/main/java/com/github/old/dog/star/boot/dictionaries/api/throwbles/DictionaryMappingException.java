package com.github.old.dog.star.boot.dictionaries.api.throwbles;

import com.github.old.dog.star.boot.dictionaries.api.DictionaryRow;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryRowMapper;

/**
 * Исключение, возникающее при ошибках преобразования данных строки словаря в объекты.
 *
 * <p>Данное исключение выбрасывается методом {@link DictionaryRow#cast(DictionaryRowMapper)}
 * когда маппер не может корректно преобразовать данные строки в целевой объект.</p>
 *
 * <h3>Типичные сценарии возникновения:</h3>
 * <ul>
 *   <li>Отсутствие обязательных атрибутов в строке данных</li>
 *   <li>Некорректные типы данных для создания целевого объекта</li>
 *   <li>Нарушение бизнес-правил при создании объекта</li>
 *   <li>Ошибки в логике маппера (например, деление на ноль)</li>
 *   <li>Недостаточно данных для инициализации сложных объектов</li>
 *   <li>Конфликты значений между различными атрибутами</li>
 * </ul>
 *
 * <h3>Пример обработки:</h3>
 * <pre>{@code
 * try {
 *     UserEntity user = row.cast(UserEntityMapper.INSTANCE);
 * } catch (DictionaryMappingException e) {
 *     logger.error("Ошибка преобразования строки в объект типа {}: {}",
 *                  e.getTargetType(), e.getMessage());
 *
 *     if (e.isMissingAttributeError()) {
 *         logger.warn("Отсутствует обязательный атрибут: {}", e.getAttributeName());
 *     }
 *
 *     // Возможна попытка создания объекта с значениями по умолчанию
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @since 1.0
 * @see DictionaryRow#cast(DictionaryRowMapper)
 * @see DictionaryRowMapper
 */
public class DictionaryMappingException extends DictionaryMetadataException {

    private final Class<?> targetType;
    private final String attributeName;
    private final DictionaryRow sourceRow;

    /**
     * Создает исключение с базовым сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause первопричина ошибки преобразования
     */
    public DictionaryMappingException(String message, Throwable cause) {
        super(message, cause);
        this.targetType = null;
        this.attributeName = null;
        this.sourceRow = null;
    }

    /**
     * Создает исключение с указанием целевого типа.
     *
     * @param targetType целевой тип объекта для преобразования
     * @param cause первопричина ошибки
     */
    public DictionaryMappingException(Class<?> targetType, Throwable cause) {
        super(String.format("Не удалось преобразовать строку словаря в объект типа '%s': %s",
                           targetType != null ? targetType.getSimpleName() : "unknown",
                           cause != null ? cause.getMessage() : "неизвестная ошибка"),
              cause);
        this.targetType = targetType;
        this.attributeName = null;
        this.sourceRow = null;
    }

    /**
     * Создает исключение с подробной информацией о контексте преобразования.
     *
     * @param targetType целевой тип объекта
     * @param attributeName имя проблемного атрибута (если известно)
     * @param sourceRow исходная строка данных
     * @param message кастомное сообщение об ошибке
     * @param cause первопричина ошибки
     */
    public DictionaryMappingException(Class<?> targetType, String attributeName,
                                      DictionaryRow sourceRow, String message, Throwable cause) {
        super(message, cause);
        this.targetType = targetType;
        this.attributeName = attributeName;
        this.sourceRow = sourceRow;
    }

    /**
     * Возвращает целевой тип объекта, в который выполнялось преобразование.
     *
     * @return класс целевого типа или {@code null}, если не указан
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Возвращает имя атрибута, связанного с ошибкой преобразования.
     *
     * @return имя проблемного атрибута или {@code null}, если не известно
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Возвращает исходную строку данных, которая не смогла быть преобразована.
     *
     * @return исходная строка или {@code null}, если не сохранена
     */
    public DictionaryRow getSourceRow() {
        return sourceRow;
    }

    /**
     * Проверяет, связана ли ошибка с отсутствием обязательного атрибута.
     *
     * @return {@code true} если ошибка вызвана отсутствием атрибута
     */
    public boolean isMissingAttributeError() {
        return getCause() instanceof DictionaryAttributeNotFoundException;
    }

    /**
     * Проверяет, связана ли ошибка с преобразованием типов данных.
     *
     * @return {@code true} если ошибка связана с конверсией типов
     */
    public boolean isTypeConversionError() {
        return getCause() instanceof DictionaryTypeConversionException;
    }

    /**
     * Проверяет, связана ли ошибка с созданием экземпляра целевого объекта.
     *
     * @return {@code true} если ошибка связана с инстанциированием
     */
    public boolean isInstantiationError() {
        return getCause() instanceof DictionaryInstantiationException;
    }

    /**
     * Возвращает информацию о драйвере источника данных из исходной строки.
     *
     * @return идентификатор драйвера или {@code null} если строка не сохранена
     */
    public String getSourceDriver() {
        return sourceRow != null ? sourceRow.getDriver() : null;
    }

    /**
     * Фабричный метод для создания исключения при отсутствии атрибута.
     *
     * @param targetType целевой тип преобразования
     * @param attributeName имя отсутствующего атрибута
     * @return новое исключение
     */
    public static DictionaryMappingException missingAttribute(Class<?> targetType, String attributeName) {
        String message = String.format(
            "Невозможно создать объект типа '%s': отсутствует обязательный атрибут '%s'",
            targetType != null ? targetType.getSimpleName() : "unknown",
            attributeName
        );

        return new DictionaryMappingException(targetType, attributeName, null, message,
                                            new DictionaryAttributeNotFoundException(attributeName));
    }

    /**
     * Фабричный метод для создания исключения при ошибке валидации.
     *
     * @param targetType целевой тип преобразования
     * @param validationMessage сообщение об ошибке валидации
     * @return новое исключение
     */
    public static DictionaryMappingException validationError(Class<?> targetType, String validationMessage) {
        String message = String.format(
            "Ошибка валидации при создании объекта типа '%s': %s",
            targetType != null ? targetType.getSimpleName() : "unknown",
            validationMessage
        );

        return new DictionaryMappingException(message, new IllegalArgumentException(validationMessage));
    }
}
