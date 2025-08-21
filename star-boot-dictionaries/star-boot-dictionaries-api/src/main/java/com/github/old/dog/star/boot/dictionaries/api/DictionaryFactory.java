package com.github.old.dog.star.boot.dictionaries.api;

import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryFactoryException;
import java.lang.reflect.Modifier;

/**
 * Фабрика для создания экземпляров справочников.
 *
 * <p>Интерфейс определяет контракт для создания настроенных экземпляров
 * {@code RawDictionary} с необходимыми зависимостями (метаданные и движок).
 * Различные реализации могут предоставлять разные стратегии создания справочников.</p>
 *
 * <h3>Реализации:</h3>
 * <ul>
 *   <li>{@code JdbcDictionaryFactory} - для работы с JDBC/JdbcTemplate</li>
 *   <li>{@code CachedDictionaryFactory} - с поддержкой кэширования</li>
 *   <li>{@code JpaDictionaryFactory} - для интеграции с JPA</li>
 * </ul>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * @Component("myDictionaryFactory")
 * public class CustomDictionaryFactory implements DictionaryFactory {
 *
 *     @Override
 *     public <T> RawDictionary<T> createDictionary(Class<T> dictionaryClass, String tableName) {
 *         // кастомная логика создания
 *         return new RawDictionary<>(metadata, engine);
 *     }
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @see Dictionary
 * @see DictionaryMetadata
 * @see DictionaryEngine
 * @since 1.0
 */
public interface DictionaryFactory {

    /**
     * Создает экземпляр словаря для указанного класса и таблицы.
     *
     * <p>Метод должен создать и настроить все необходимые зависимости:
     * метаданные класса и движок для работы с данными. Реализации могут
     * добавлять дополнительную функциональность (кэширование, валидацию,
     * метрики и т.д.).</p>
     *
     * <h4>Требования к реализации:</h4>
     * <ul>
     *   <li>Создавать метаданные для указанного класса</li>
     *   <li>Создавать движок для работы с указанной таблицей</li>
     *   <li>Возвращать полностью настроенный экземпляр RawDictionary</li>
     *   <li>Обрабатывать ошибки и логировать процесс создания</li>
     * </ul>
     *
     * @param <T>             тип словарной сущности
     * @param dictionaryClass класс словарной сущности, не должен быть {@code null}
     * @param tableName       имя таблицы базы данных, не должно быть {@code null} или пустым
     * @return настроенный экземпляр RawDictionary, никогда не {@code null}
     * @throws IllegalArgumentException   если параметры равны {@code null} или невалидны
     * @throws DictionaryFactoryException если не удается создать словарь
     * @implSpec реализации должны:
     * <ul>
     *   <li>Валидировать входные параметры</li>
     *   <li>Создавать потокобезопасные экземпляры</li>
     *   <li>Логировать процесс создания</li>
     *   <li>Обеспечивать корректную обработку ошибок</li>
     * </ul>
     */
    <T> Dictionary<T> createDictionary(Class<T> dictionaryClass, String tableName);

    /**
     * Возвращает описательное имя фабрики для логирования и отладки.
     *
     * @return имя фабрики, например "JDBC Dictionary Factory"
     */
    default String getFactoryName() {
        return getClass().getSimpleName();
    }

    /**
     * Проверяет поддержку создания словаря для указанного класса.
     *
     * <p>Позволяет фабрике отклонить создание словаря, если класс
     * не поддерживается или не соответствует требованиям.</p>
     *
     * @param dictionaryClass класс для проверки
     * @return {@code true} если фабрика может создать словарь для этого класса
     */
    default boolean supports(Class<?> dictionaryClass) {
        if (dictionaryClass == null) {
            return false;
        }

        int modifiers = dictionaryClass.getModifiers();

        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
            return false;
        }

        try {
            // Проверяем наличие конструктора по умолчанию
            dictionaryClass.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

}
