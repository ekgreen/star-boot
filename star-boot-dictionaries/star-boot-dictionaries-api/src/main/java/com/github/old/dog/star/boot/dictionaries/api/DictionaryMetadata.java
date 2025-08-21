package com.github.old.dog.star.boot.dictionaries.api;

import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAccessException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAttributeNotFoundException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryInstantiationException;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryTypeConversionException;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Интерфейс для работы с метаданными словарных объектов.
 *
 * <p>Предоставляет унифицированный API для получения информации о структуре
 * словарных классов, их атрибутах и методах создания экземпляров.
 * Основная цель - абстрагировать работу с reflection и обеспечить
 * типобезопасный доступ к метаданным.</p>
 *
 * <p>Словарные объекты - это классы, которые представляют справочную информацию
 * и имеют четко определенную структуру с именованными атрибутами.</p>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * DictionaryMetadata metadata = DictionaryMetadataFactory.create(UserEntity.class);
 *
 * // Получение информации о классе
 * Class<?> clazz = metadata.getDictionaryClass();
 * String tableName = metadata.getDictionaryTable();
 * Set<String> attributes = metadata.getDictionaryAttributes();
 *
 * // Создание и заполнение экземпляра
 * Object instance = metadata.newInstance();
 * metadata.set("name", instance, "John Doe");
 * metadata.set("age", instance, 30);
 * }</pre>
 *
 * @author AI Assistant
 * @since 1.0
 */
public interface DictionaryMetadata {

    /**
     * Возвращает класс словарного объекта.
     *
     * <p>Класс должен представлять словарную сущность с определенной структурой
     * и набором атрибутов. Обычно это Entity классы, DTO или другие объекты
     * предметной области.</p>
     *
     * @return класс словарного объекта, никогда не {@code null}
     * @throws IllegalStateException если метаданные не инициализированы корректно
     */
    Class<?> getDictionaryClass();

    /**
     * Возвращает имя таблицы базы данных, связанной со словарным объектом.
     *
     * <p>Имя таблицы может быть получено из аннотаций (@Table, @Entity),
     * конфигурации или соглашений по именованию. Если таблица не определена,
     * может возвращаться имя класса в нижнем регистре.</p>
     *
     * @return имя таблицы или {@code null}, если таблица не определена
     * @implNote реализации могут использовать различные стратегии для определения
     * имени таблицы: аннотации JPA, Spring Data, кастомная логика
     */
    String getDictionaryTable();

    /**
     * Возвращает набор имен всех доступных атрибутов словарного объекта.
     *
     * <p>Атрибуты представляют собой поля или свойства объекта, доступные
     * для чтения и записи. Включаются только те атрибуты, которые могут
     * быть использованы для работы с данными.</p>
     *
     * <p>Порядок атрибутов в наборе не гарантируется, но реализации могут
     * обеспечивать стабильный порядок для предсказуемости.</p>
     *
     * @return неизменяемый набор имен атрибутов, может быть пустым, но не {@code null}
     * @implNote реализации должны кэширovat результат для производительности
     */
    Set<String> getDictionaryAttributes();

    /**
     * Создает новый экземпляр словарного объекта.
     *
     * <p>Экземпляр создается с использованием конструктора по умолчанию.
     * Все поля инициализируются значениями по умолчанию согласно спецификации Java.</p>
     *
     * @return новый экземпляр словарного объекта
     * @throws DictionaryInstantiationException если не удается создать экземпляр
     *                                          (например, отсутствует конструктор по умолчанию, класс абстрактный,
     *                                          недостаточно прав доступа)
     * @implNote реализации могут использовать различные механизмы создания:
     * reflection, фабричные методы, dependency injection
     */
    <T> T newInstance() throws DictionaryInstantiationException;

    /**
     * Устанавливает значение атрибута для указанного объекта.
     *
     * <p>Метод выполняет присвоение значения атрибуту с именем {@code attributeName}
     * в объекте {@code subject}. Поддерживается автоматическое преобразование
     * совместимых типов данных.</p>
     *
     * <h4>Преобразования типов:</h4>
     * <ul>
     *   <li>Примитивы и их обертки (int ↔ Integer, boolean ↔ Boolean)</li>
     *   <li>Числовые типы с расширением (int → long, float → double)</li>
     *   <li>Строки в примитивы (String → int, String → boolean)</li>
     *   <li>Null значения для nullable полей</li>
     * </ul>
     *
     * @param attributeName имя атрибута для установки значения, не должно быть {@code null}
     * @param subject       объект, в котором устанавливается значение, не должен быть {@code null}
     * @param value         значение для установки, может быть {@code null}
     * @throws IllegalArgumentException             если {@code attributeName} или {@code subject} равны {@code null}
     * @throws DictionaryAttributeNotFoundException если атрибут с указанным именем не найден
     * @throws DictionaryTypeConversionException    если невозможно преобразовать {@code value}
     *                                              к типу атрибута
     * @throws DictionaryAccessException            если нет прав доступа к атрибуту
     * @implSpec реализации должны:
     * <ul>
     *   <li>Валидировать входные параметры</li>
     *   <li>Проверять существование атрибута</li>
     *   <li>Выполнять безопасное преобразование типов</li>
     *   <li>Обеспечивать атомарность операции</li>
     * </ul>
     */
    void set(String attributeName, Object subject, Object value)
            throws DictionaryAttributeNotFoundException, DictionaryTypeConversionException, DictionaryAccessException;

    /**
     * Получает значение атрибута из указанного объекта.
     *
     * <p>Метод извлекает значение атрибута с именем {@code attributeName}
     * из объекта {@code subject}. Возвращаемое значение имеет тот же тип,
     * что и поле в классе.</p>
     *
     * @param attributeName имя атрибута для получения значения, не должно быть {@code null}
     * @param subject       объект, из которого извлекается значение, не должен быть {@code null}
     * @return значение атрибута, может быть {@code null}
     * @throws IllegalArgumentException             если {@code attributeName} или {@code subject} равны {@code null}
     * @throws DictionaryAttributeNotFoundException если атрибут с указанным именем не найден
     * @throws DictionaryAccessException            если нет прав доступа к атрибуту
     */
    Object get(String attributeName, Object subject) throws DictionaryAttributeNotFoundException, DictionaryAccessException;

    /**
     * Проверяет существование атрибута с указанным именем.
     *
     * @param attributeName имя атрибута для проверки
     * @return {@code true} если атрибут существует, {@code false} иначе
     * @throws IllegalArgumentException если {@code attributeName} равно {@code null}
     */
    boolean hasAttribute(String attributeName);

    /**
     * Возвращает тип атрибута.
     *
     * @param attributeName имя атрибута
     * @return класс типа атрибута
     * @throws DictionaryAttributeNotFoundException если атрибут не найден
     */
    Class<?> getAttributeType(String attributeName) throws DictionaryAttributeNotFoundException;

    /**
     * Возвращает информацию о поле, связанном с атрибутом.
     *
     * <p>Этот метод предоставляет низкоуровневый доступ к reflection API
     * для продвинутых случаев использования.</p>
     *
     * @param attributeName имя атрибута
     * @return Optional содержащий Field, если атрибут найден
     * @implNote этот метод может отсутствовать в реализациях, не основанных на reflection
     */
    default Field getAttributeField(String attributeName) {
        return null;
    }

    /**
     * Проверяет совместимость объекта с данными метаданными.
     *
     * @param object объект для проверки
     * @return {@code true} если объект совместим с метаданными
     */
    default boolean isCompatible(Object object) {
        return object != null && getDictionaryClass().isAssignableFrom(object.getClass());
    }
}
