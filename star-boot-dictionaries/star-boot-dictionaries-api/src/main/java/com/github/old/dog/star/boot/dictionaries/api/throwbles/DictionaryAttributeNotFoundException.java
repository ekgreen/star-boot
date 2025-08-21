package com.github.old.dog.star.boot.dictionaries.api.throwbles;


import com.github.old.dog.star.boot.dictionaries.api.DictionaryMetadata;

/**
 * Исключение, возникающее при попытке обращения к несуществующему атрибуту словарного объекта.
 *
 * <p>Данное исключение выбрасывается методами {@link DictionaryMetadata#set},
 * {@link DictionaryMetadata#get}, {@link DictionaryMetadata#getAttributeType} и другими,
 * когда указанное имя атрибута не найдено в структуре словарного класса.</p>
 *
 * <h3>Типичные сценарии возникновения:</h3>
 * <ul>
 *   <li>Опечатка в имени атрибута</li>
 *   <li>Изменение структуры класса без обновления кода</li>
 *   <li>Обращение к приватным или transient полям, исключенным из метаданных</li>
 *   <li>Попытка доступа к статическим полям</li>
 *   <li>Использование устаревших имен атрибутов после рефакторинга</li>
 * </ul>
 *
 * <h3>Пример обработки:</h3>
 * <pre>{@code
 * try {
 *     metadata.set("userNme", user, "John"); // опечатка в "userName"
 * } catch (DictionaryAttributeNotFoundException e) {
 *     logger.warn("Атрибут '{}' не найден. Доступные атрибуты: {}",
 *                 e.getAttributeName(), metadata.getDictionaryAttributes());
 *     // Возможна попытка автокоррекции или предложение альтернатив
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @since 1.0
 * @see DictionaryMetadata#set(String, Object, Object)
 * @see DictionaryMetadata#get(String, Object)
 * @see DictionaryMetadata#getAttributeType(String)
 */
public class DictionaryAttributeNotFoundException extends DictionaryMetadataException {

    private final String attributeName;
    private final Class<?> dictionaryClass;

    /**
     * Создает исключение для указанного имени атрибута.
     *
     * @param attributeName имя не найденного атрибута
     */
    public DictionaryAttributeNotFoundException(String attributeName) {
        super(String.format("Атрибут '%s' не найден в словарном объекте", attributeName));
        this.attributeName = attributeName;
        this.dictionaryClass = null;
    }

    /**
     * Создает исключение с указанием класса словаря.
     *
     * @param attributeName имя не найденного атрибута
     * @param dictionaryClass класс словарного объекта
     */
    public DictionaryAttributeNotFoundException(String attributeName, Class<?> dictionaryClass) {
        super(String.format("Атрибут '%s' не найден в словарном объекте класса '%s'",
                attributeName,
                dictionaryClass != null ? dictionaryClass.getSimpleName() : "unknown"));
        this.attributeName = attributeName;
        this.dictionaryClass = dictionaryClass;
    }

    /**
     * Создает исключение с кастомным сообщением.
     *
     * @param attributeName имя не найденного атрибута
     * @param dictionaryClass класс словарного объекта
     * @param message кастомное сообщение об ошибке
     */
    public DictionaryAttributeNotFoundException(String attributeName, Class<?> dictionaryClass, String message) {
        super(message);
        this.attributeName = attributeName;
        this.dictionaryClass = dictionaryClass;
    }

    /**
     * Возвращает имя не найденного атрибута.
     *
     * @return имя атрибута или {@code null}, если не указано
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Возвращает класс словарного объекта, в котором искался атрибут.
     *
     * @return класс словаря или {@code null}, если не указан
     */
    public Class<?> getDictionaryClass() {
        return dictionaryClass;
    }

    /**
     * Создает исключение с предложением возможных альтернатив.
     *
     * @param attributeName имя не найденного атрибута
     * @param dictionaryClass класс словарного объекта
     * @param availableAttributes доступные атрибуты для подсказки
     * @return новое исключение с расширенным сообщением
     */
    public static DictionaryAttributeNotFoundException withSuggestions(
            String attributeName,
            Class<?> dictionaryClass,
            java.util.Set<String> availableAttributes) {

        String message = String.format(
                "Атрибут '%s' не найден в классе '%s'. Доступные атрибуты: %s",
                attributeName,
                dictionaryClass != null ? dictionaryClass.getSimpleName() : "unknown",
                availableAttributes != null ? availableAttributes : "[]"
        );

        return new DictionaryAttributeNotFoundException(attributeName, dictionaryClass, message);
    }
}
