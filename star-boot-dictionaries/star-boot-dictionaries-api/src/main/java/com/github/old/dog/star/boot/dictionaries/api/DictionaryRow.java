package com.github.old.dog.star.boot.dictionaries.api;

import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAttributeNotFoundException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryMappingException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryTypeConversionException;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Интерфейс представляющий строку данных из словарной таблицы.
 *
 * <p>Предоставляет унифицированный доступ к данным словарных записей независимо
 * от источника данных (JDBC, JPA, файлы, веб-сервисы и т.д.). Каждая строка
 * содержит стандартный набор атрибутов словаря и дополнительные пользовательские
 * атрибуты.</p>
 *
 * <h3>Стандартные атрибуты словаря:</h3>
 * <ul>
 *   <li><strong>id</strong> - уникальный идентификатор записи</li>
 *   <li><strong>code</strong> - код записи для программного использования</li>
 *   <li><strong>name</strong> - отображаемое название записи</li>
 *   <li><strong>definition</strong> - описание или определение записи</li>
 *   <li><strong>creationTimestamp</strong> - время создания записи</li>
 * </ul>
 *
 * <h3>Работа с атрибутами:</h3>
 * <p>Интерфейс поддерживает как прямой доступ к значениям атрибутов, так и
 * их преобразование с помощью специальных мапперов. Это позволяет гибко
 * обрабатывать данные различных типов.</p>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * DictionaryRow row = dictionaryEngine.getById(123);
 *
 * // Доступ к стандартным атрибутам
 * int id = row.getId();
 * String code = row.getCode();
 * String name = row.getName();
 *
 * // Доступ к произвольным атрибутам
 * String customValue = row.attribute("custom_field");
 * BigDecimal price = row.attribute("price", BigDecimalMapper.INSTANCE);
 *
 * // Преобразование всей строки в объект
 * UserEntity user = row.cast(UserEntityMapper.INSTANCE);
 * }</pre>
 *
 * <h3>Типобезопасность:</h3>
 * <p>Методы для работы с атрибутами используют generic типы для обеспечения
 * типобезопасности на этапе компиляции. Однако следует быть осторожным
 * с приведением типов и использовать мапперы для сложных преобразований.</p>
 *
 * @author AI Assistant
 * @since 1.0
 * @see DictionaryEngine
 * @see DictionaryAttributeMapper
 * @see DictionaryRowMapper
 */
public interface DictionaryRow {

    /**
     * Возвращает уникальный идентификатор записи словаря.
     *
     * <p>Идентификатор должен быть уникальным в рамках конкретной словарной
     * таблицы и не изменяться в течение жизненного цикла записи.</p>
     *
     * @return уникальный идентификатор записи
     */
    int getId();

    /**
     * Возвращает код записи словаря.
     *
     * <p>Код предназначен для программного использования и должен быть
     * уникальным в рамках словаря. Обычно используется для связи с другими
     * объектами системы вместо числового идентификатора.</p>
     *
     * @return код записи или {@code null}, если не задан
     */
    String getCode();

    /**
     * Возвращает отображаемое название записи словаря.
     *
     * <p>Название предназначено для показа пользователю в интерфейсе.
     * Может быть локализованным в зависимости от реализации.</p>
     *
     * @return название записи или {@code null}, если не задано
     */
    String getName();

    /**
     * Возвращает описание или определение записи словаря.
     *
     * <p>Содержит подробное описание записи, её назначение или
     * дополнительную информацию для пользователя.</p>
     *
     * @return описание записи или {@code null}, если не задано
     */
    String getDefinition();

    /**
     * Возвращает время создания записи словаря.
     *
     * <p>Содержит метку времени, когда запись была создана в системе.
     * Используется для аудита и отслеживания изменений.</p>
     *
     * @return время создания записи или {@code null}, если не задано
     */
    LocalDateTime getCreationTimestamp();

    /**
     * Возвращает значение произвольного атрибута записи.
     *
     * <p>Позволяет получить доступ к любому атрибуту записи по его имени.
     * Возвращаемый тип должен соответствовать реальному типу данных атрибута.</p>
     *
     * <p><strong>Предупреждение:</strong> Метод выполняет небезопасное приведение
     * типов. Рекомендуется использовать типизированные методы или мапперы
     * для сложных типов данных.</p>
     *
     * @param <T> ожидаемый тип возвращаемого значения
     * @param name имя атрибута
     * @return значение атрибута приведенное к типу T, может быть {@code null}
     * @throws DictionaryAttributeNotFoundException если атрибут с указанным именем не найден
     * @throws ClassCastException если значение не может быть приведено к типу T
     */
    <T> T attribute(String name);

    /**
     * Возвращает тип данных указанного атрибута.
     *
     * <p>Тип представлен в виде константы из {@link java.sql.Types} для
     * совместимости с JDBC типами данных.</p>
     *
     * @param name имя атрибута
     * @return код типа данных согласно java.sql.Types
     * @throws DictionaryAttributeNotFoundException если атрибут с указанным именем не найден
     * @see java.sql.Types
     */
    int getAttributeType(String name);

    /**
     * Возвращает значение атрибута с применением указанного маппера.
     *
     * <p>Позволяет безопасно преобразовать значение атрибута к нужному типу
     * с помощью специального маппера. Маппер получает доступ как к значению,
     * так и к информации о типе данных.</p>
     *
     * @param <T> тип результата преобразования
     * @param name имя атрибута
     * @param mapper маппер для преобразования значения
     * @return преобразованное значение атрибута
     * @throws DictionaryAttributeNotFoundException если атрибут с указанным именем не найден
     * @throws DictionaryTypeConversionException если маппер не может выполнить преобразование
     */
    <T> T attribute(String name, DictionaryAttributeMapper<T> mapper);

    /**
     * Преобразует всю строку словаря в объект указанного типа.
     *
     * <p>Позволяет создать типизированный объект на основе данных всей строки.
     * Полезно для преобразования в Entity объекты, DTO или другие структуры данных.</p>
     *
     * @param <T> тип результирующего объекта
     * @param rowMapper маппер для преобразования строки
     * @return объект типа T, созданный на основе данных строки
     * @throws DictionaryMappingException если маппер не может выполнить преобразование
     */
    <T> T cast(DictionaryRowMapper<T> rowMapper) throws DictionaryMappingException;

    /**
     * Проверяет существование атрибута с указанным именем в строке словаря.
     *
     * <p>Этот метод позволяет безопасно проверить наличие атрибута перед
     * попыткой получения его значения, что помогает избежать исключений
     * {@link DictionaryAttributeNotFoundException}.</p>
     *
     * <h3>Пример использования:</h3>
     * <pre>{@code
     * if (row.hasAttribute("optional_field")) {
     *     String value = row.attribute("optional_field");
     *     // Обработка значения
     * } else {
     *     // Использование значения по умолчанию
     *     String value = "default_value";
     * }
     * }</pre>
     *
     * @param attributeName имя атрибута для проверки, не должно быть {@code null}
     * @return {@code true} если атрибут существует в строке, {@code false} иначе
     * @throws IllegalArgumentException если {@code attributeName} равно {@code null}
     * @see #attribute(String)
     * @see #getAttributeNames()
     */
    boolean hasAttribute(String attributeName);

    /**
     * Возвращает неизменяемый набор имен всех доступных атрибутов в строке.
     *
     * <p>Включает все атрибуты, доступные в данной строке словаря, включая
     * стандартные атрибуты (id, code, name, definition, creation_timestamp)
     * и дополнительные пользовательские атрибуты.</p>
     *
     * <p>Набор может быть использован для итерации по всем атрибутам,
     * динамической обработки данных или создания метаинформации о структуре строки.</p>
     *
     * <h3>Пример использования:</h3>
     * <pre>{@code
     * Set<String> attributes = row.getAttributeNames();
     *
     * // Вывод всех атрибутов и их значений
     * for (String attrName : attributes) {
     *     Object value = row.attribute(attrName);
     *     System.out.println(attrName + " = " + value);
     * }
     *
     * // Проверка наличия определенных атрибутов
     * boolean hasCustomField = attributes.contains("custom_field");
     * }</pre>
     *
     * @return неизменяемый набор имен атрибутов, никогда не {@code null}, может быть пустым
     * @see #hasAttribute(String)
     * @see #getAttributeCount()
     */
    Set<String> getAttributeNames();

    /**
     * Возвращает общее количество атрибутов в строке словаря.
     *
     * <p>Подсчитывает все доступные атрибуты, включая как стандартные
     * атрибуты словаря, так и пользовательские поля. Полезно для оценки
     * размера данных, валидации структуры или оптимизации обработки.</p>
     *
     * <h3>Связь с другими методами:</h3>
     * <p>Значение, возвращаемое этим методом, всегда равно размеру множества,
     * возвращаемого методом {@link #getAttributeNames()}:</p>
     * <pre>{@code
     * assert row.getAttributeCount() == row.getAttributeNames().size();
     * }</pre>
     *
     * <h3>Пример использования:</h3>
     * <pre>{@code
     * int attrCount = row.getAttributeCount();
     *
     * if (attrCount == 0) {
     *     throw new IllegalStateException("Строка словаря не содержит атрибутов");
     * }
     *
     * // Предварительное выделение памяти под коллекцию
     * List<Object> values = new ArrayList<>(attrCount);
     *
     * // Проверка на минимально необходимое количество атрибутов
     * if (attrCount < 5) {
     *     logger.warn("Строка содержит менее 5 атрибутов: {}", attrCount);
     * }
     * }</pre>
     *
     * @return количество атрибутов в строке, всегда >= 0
     * @see #getAttributeNames()
     * @see #hasAttribute(String)
     */
    int getAttributeCount();


    /**
     * Возвращает идентификатор драйвера источника данных.
     *
     * <p>Позволяет определить, из какого источника данных была получена строка.
     * Может использоваться для выбора специфичной логики обработки или
     * оптимизации запросов.</p>
     *
     * <p>Примеры возможных значений:</p>
     * <ul>
     *   <li>"jdbc" - данные из реляционной БД через JDBC</li>
     *   <li>"jpa" - данные через JPA/Hibernate</li>
     *   <li>"json" - данные из JSON файла или API</li>
     *   <li>"xml" - данные из XML источника</li>
     *   <li>"memory" - данные из in-memory кэша</li>
     * </ul>
     *
     * @return идентификатор драйвера источника данных
     */
    String getDriver();
}
