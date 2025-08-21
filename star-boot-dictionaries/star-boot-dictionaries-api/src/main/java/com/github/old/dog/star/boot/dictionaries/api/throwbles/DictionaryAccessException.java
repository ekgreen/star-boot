package com.github.old.dog.star.boot.dictionaries.api.throwbles;

import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;

/**
 * Исключение, возникающее при проблемах с доступом к атрибутам словарного объекта.
 *
 * <p>Данное исключение выбрасывается методами {@link DictionaryMetadata#set} и
 * {@link DictionaryMetadata#get}, когда операция не может быть выполнена из-за
 * ограничений доступа на уровне JVM или безопасности.</p>
 *
 * <h3>Типичные причины возникновения:</h3>
 * <ul>
 *   <li>Поле объявлено как private без возможности изменения доступности</li>
 *   <li>SecurityManager запрещает доступ к полю через reflection</li>
 *   <li>Поле объявлено как final и не может быть изменено</li>
 *   <li>Проблемы с ClassLoader'ом и видимостью классов</li>
 *   <li>Поле находится в sealed или module-restricted пакете</li>
 *   <li>JVM запрещает reflection доступ к internal API</li>
 * </ul>
 *
 * <h3>Рекомендации по устранению:</h3>
 * <ul>
 *   <li>Проверить модификаторы доступа поля (private, protected, package-private)</li>
 *   <li>Убедиться что поле не является final для операций записи</li>
 *   <li>Проверить настройки SecurityManager если используется</li>
 *   <li>Для модульных приложений - проверить module-info.java</li>
 *   <li>Рассмотреть использование геттеров/сеттеров вместо прямого доступа к полям</li>
 * </ul>
 *
 * <h3>Пример обработки:</h3>
 * <pre>{@code
 * try {
 *     metadata.set("privateField", object, value);
 * } catch (DictionaryAccessException e) {
 *     if (e.isSecurityRelated()) {
 *         logger.error("Доступ к полю '{}' запрещен политикой безопасности",
 *                      e.getAttributeName());
 *     } else {
 *         logger.warn("Невозможно получить доступ к полю '{}': {}",
 *                     e.getAttributeName(), e.getMessage());
 *     }
 *     // Возможна попытка альтернативного доступа через публичные методы
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @see DictionaryMetadata#set(String, Object, Object)
 * @see DictionaryMetadata#get(String, Object)
 * @since 1.0
 */
public class DictionaryAccessException extends DictionaryMetadataException {

    private final String attributeName;
    private final AccessType accessType;
    private final Class<?> targetClass;

    /**
     * Создает исключение с базовым сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause   первопричина ошибки доступа
     */
    public DictionaryAccessException(String message, Throwable cause) {
        super(message, cause);
        this.attributeName = null;
        this.accessType = AccessType.UNKNOWN;
        this.targetClass = null;
    }

    /**
     * Создает исключение с указанием атрибута и типа доступа.
     *
     * @param attributeName имя атрибута, к которому не удалось получить доступ
     * @param accessType    тип операции доступа (чтение/запись)
     * @param cause         первопричина ошибки
     */
    public DictionaryAccessException(String attributeName, AccessType accessType, Throwable cause) {
        super(String.format("Нет доступа для %s атрибута '%s': %s",
                accessType.getDescription(),
                attributeName,
                cause != null ? cause.getMessage() : "неизвестная причина"),
            cause);
        this.attributeName = attributeName;
        this.accessType = accessType;
        this.targetClass = null;
    }

    /**
     * Создает исключение с полной информацией о контексте.
     *
     * @param attributeName имя атрибута
     * @param accessType    тип операции доступа
     * @param targetClass   класс, содержащий атрибут
     * @param cause         первопричина ошибки
     */
    public DictionaryAccessException(String attributeName, AccessType accessType,
                                     Class<?> targetClass, Throwable cause) {
        super(String.format("Нет доступа для %s атрибута '%s' в классе '%s': %s",
                accessType.getDescription(),
                attributeName,
                targetClass != null ? targetClass.getSimpleName() : "unknown",
                cause != null ? cause.getMessage() : "неизвестная причина"),
            cause);
        this.attributeName = attributeName;
        this.accessType = accessType;
        this.targetClass = targetClass;
    }

    /**
     * Возвращает имя атрибута, к которому не удалось получить доступ.
     *
     * @return имя атрибута или {@code null}, если не указано
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Возвращает тип операции доступа, которая вызвала ошибку.
     *
     * @return тип доступа
     */
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     * Возвращает класс, содержащий проблемный атрибут.
     *
     * @return класс или {@code null}, если не указан
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Проверяет, связана ли ошибка с политикой безопасности.
     *
     * @return {@code true} если ошибка вызвана SecurityManager
     */
    public boolean isSecurityRelated() {
        return getCause() instanceof SecurityException;
    }

    /**
     * Проверяет, связана ли ошибка с попыткой изменения final поля.
     *
     * @return {@code true} если ошибка связана с final полем
     */
    public boolean isFinalFieldError() {
        return accessType == AccessType.WRITE
               && getCause() instanceof IllegalAccessException
               && getCause().getMessage() != null
               && getCause().getMessage().toLowerCase().contains("final");
    }

    /**
     * Типы операций доступа к атрибутам.
     */
    public enum AccessType {
        /**
         * Операция чтения значения атрибута
         */
        READ("чтения"),

        /**
         * Операция записи значения атрибута
         */
        WRITE("записи"),

        /**
         * Тип операции неизвестен
         */
        UNKNOWN("доступа");

        private final String description;

        AccessType(String description) {
            this.description = description;
        }

        /**
         * Возвращает описание типа операции на русском языке.
         *
         * @return описание операции
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Фабричный метод для создания исключения при ошибке чтения.
     *
     * @param attributeName имя атрибута
     * @param targetClass   класс-владелец атрибута
     * @param cause         причина ошибки
     * @return новое исключение
     */
    public static DictionaryAccessException readError(String attributeName, Class<?> targetClass, Throwable cause) {
        return new DictionaryAccessException(attributeName, AccessType.READ, targetClass, cause);
    }

    /**
     * Фабричный метод для создания исключения при ошибке записи.
     *
     * @param attributeName имя атрибута
     * @param targetClass   класс-владелец атрибута
     * @param cause         причина ошибки
     * @return новое исключение
     */
    public static DictionaryAccessException writeError(String attributeName, Class<?> targetClass, Throwable cause) {
        return new DictionaryAccessException(attributeName, AccessType.WRITE, targetClass, cause);
    }
}
