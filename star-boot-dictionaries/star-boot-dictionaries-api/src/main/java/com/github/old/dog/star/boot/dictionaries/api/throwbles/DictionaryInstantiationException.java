package com.github.old.dog.star.boot.dictionaries.api.throwbles;


import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;

/**
 * Исключение, возникающее при ошибке создания экземпляра словарного объекта.
 *
 * <p>Данное исключение выбрасывается методом {@link DictionaryMetadata#newInstance()}
 * в случаях, когда невозможно создать новый экземпляр словарного класса.</p>
 *
 * <h3>Типичные причины возникновения:</h3>
 * <ul>
 *   <li>Отсутствие конструктора по умолчанию (без параметров)</li>
 *   <li>Конструктор по умолчанию является приватным или защищенным</li>
 *   <li>Класс является абстрактным или интерфейсом</li>
 *   <li>Класс является внутренним не-статическим классом</li>
 *   <li>Недостаточно прав доступа для создания экземпляра</li>
 *   <li>Ошибка в конструкторе (например, выброшено исключение)</li>
 *   <li>Класс не найден в classpath во время выполнения</li>
 * </ul>
 *
 * <h3>Пример обработки:</h3>
 * <pre>{@code
 * try {
 *     Object instance = metadata.newInstance();
 * } catch (DictionaryInstantiationException e) {
 *     logger.error("Не удалось создать экземпляр словарного объекта: {}",
 *                  e.getMessage(), e);
 *     // Попытка создания альтернативным способом или уведомление пользователя
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @since 1.0
 * @see DictionaryMetadata#newInstance()
 */
public class DictionaryInstantiationException extends DictionaryMetadataException {

    private final Class<?> targetClass;

    /**
     * Создает исключение с указанным сообщением и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause первопричина ошибки создания экземпляра
     */
    public DictionaryInstantiationException(String message, Throwable cause) {
        super(message, cause);
        this.targetClass = null;
    }

    /**
     * Создает исключение с указанием класса, для которого не удалось создать экземпляр.
     *
     * @param targetClass класс, для которого произошла ошибка создания экземпляра
     * @param cause первопричина ошибки
     */
    public DictionaryInstantiationException(Class<?> targetClass, Throwable cause) {
        super(String.format("Не удалось создать экземпляр класса '%s': %s",
                        targetClass != null ? targetClass.getName() : "null",
                        cause != null ? cause.getMessage() : "неизвестная ошибка"),
                cause);
        this.targetClass = targetClass;
    }

    /**
     * Создает исключение с подробным сообщением, классом и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param targetClass класс, для которого произошла ошибка
     * @param cause первопричина ошибки
     */
    public DictionaryInstantiationException(String message, Class<?> targetClass, Throwable cause) {
        super(message, cause);
        this.targetClass = targetClass;
    }

    /**
     * Возвращает класс, для которого не удалось создать экземпляр.
     *
     * @return класс цели или {@code null}, если не указан
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Проверяет, связано ли исключение с указанным классом.
     *
     * @param clazz класс для проверки
     * @return {@code true} если исключение связано с указанным классом
     */
    public boolean isRelatedTo(Class<?> clazz) {
        return targetClass != null && targetClass.equals(clazz);
    }
}
