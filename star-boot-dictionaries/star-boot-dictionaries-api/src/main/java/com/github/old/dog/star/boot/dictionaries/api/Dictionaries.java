package com.github.old.dog.star.boot.dictionaries.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * Интерфейс фасада для работы со всеми доступными в runtime справочниками.
 *
 * <p>Представляет единую точку входа для доступа к справочным данным различных типов.
 * Обеспечивает стандартизированный способ получения данных из справочников с поддержкой
 * обработки ошибок через механизмы fallback и значений по умолчанию.</p>
 *
 * <p><strong>Основные возможности:</strong></p>
 * <ul>
 *   <li>Получение данных по типу класса справочника</li>
 *   <li>Получение данных по названию справочника</li>
 *   <li>Поддержка fallback-сценариев при ошибках</li>
 *   <li>Поддержка значений по умолчанию</li>
 *   <li>Унифицированный интерфейс для всех операций со справочниками</li>
 * </ul>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>{@code
 * @Autowired
 * private Dictionaries dictionaries;
 *
 * // Получение всех записей по типу
 * List<SubjectSectorEntity> sectors = dictionaries.getAll(SubjectSectorEntity.class);
 *
 * // Получение записи по ID с fallback
 * Optional<SubjectSectorEntity> sector = dictionaries.getByIdWithFallback(
 *     SubjectSectorEntity.class,
 *     1,
 *     ex -> Optional.empty()
 * );
 *
 * // Получение записи с значением по умолчанию
 * SubjectSectorEntity defaultSector = dictionaries.getByCodeOrDefault(
 *     "sectors",
 *     "BANK",
 *     () -> new SubjectSectorEntity("DEFAULT", "По умолчанию")
 * );
 * }</pre>
 *
 * @author AI Assistant
 * @since 1.0.0
 * @see Dictionary
 */
public interface Dictionaries {

    /**
     * Возвращает все записи справочника по типу класса.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @return список всех записей справочника
     * @throws IllegalArgumentException если dictionaryType равен null
     */
    <T> List<T> getAll(Class<T> dictionaryType);

    /**
     * Возвращает все записи справочника по названию.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @return список всех записей справочника
     * @throws IllegalArgumentException если dictionaryName пустой или равен null
     */
    <T> List<T> getAll(String dictionaryName);

    /**
     * Возвращает все записи справочника по типу класса с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param fallback функция обработки ошибок
     * @return список записей или результат fallback функции
     */
    default <T> List<T> getAllWithFallback(Class<T> dictionaryType, Function<Exception, List<T>> fallback) {
        try {
            return getAll(dictionaryType);
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getAll', тип = {}",
                    dictionaryType, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Возвращает все записи справочника по типу класса или значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param defaultValue поставщик значения по умолчанию
     * @return список записей или значение по умолчанию
     */
    default <T> List<T> getAllOrDefault(Class<T> dictionaryType, Supplier<List<T>> defaultValue) {
        return this.getAllWithFallback(dictionaryType, _ -> defaultValue.get());
    }

    /**
     * Возвращает все записи справочника по названию с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param fallback функция обработки ошибок
     * @return список записей или результат fallback функции
     */
    default <T> List<T> getAllWithFallback(String dictionaryName, Function<Exception, List<T>> fallback) {
        try {
            return getAll(dictionaryName);
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getAll', название = {}",
                    dictionaryName, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Возвращает все записи справочника по названию или значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param defaultValue поставщик значения по умолчанию
     * @return список записей или значение по умолчанию
     */
    default <T> List<T> getAllOrDefault(String dictionaryName, Supplier<List<T>> defaultValue) {
        return this.getAllWithFallback(dictionaryName, _ -> defaultValue.get());
    }

    /**
     * Находит запись справочника по ID и типу класса.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param id уникальный идентификатор
     * @return найденная запись или пустой Optional
     */
    <T> Optional<T> getById(Class<T> dictionaryType, int id);

    /**
     * Находит запись справочника по ID и названию.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param id уникальный идентификатор
     * @return найденная запись или пустой Optional
     */
    <T> Optional<T> getById(String dictionaryName, int id);

    /**
     * Находит запись справочника по ID с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param id уникальный идентификатор
     * @param fallback функция обработки ошибок
     * @return найденная запись или результат fallback функции
     */
    default <T> T getByIdWithFallback(Class<T> dictionaryType, int id, Function<Exception, T> fallback) {
        try {
            Optional<T> byId = this.getById(dictionaryType, id);
            return byId.orElseGet(() -> fallback.apply(null));
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getById', тип = {}, id = {}",
                    dictionaryType, id, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Находит запись справочника по ID или возвращает значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param id уникальный идентификатор
     * @param defaultValue поставщик значения по умолчанию
     * @return найденная запись или значение по умолчанию
     */
    default <T> T getByIdOrDefault(Class<T> dictionaryType, int id, Supplier<T> defaultValue) {
        return this.getByIdWithFallback(dictionaryType, id, _ -> defaultValue.get());
    }

    /**
     * Находит запись справочника по ID с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param id уникальный идентификатор
     * @param fallback функция обработки ошибок
     * @return найденная запись или результат fallback функции
     */
    default <T> T getByIdWithFallback(String dictionaryName, int id, Function<Exception, T> fallback) {
        try {
            Optional<T> byId = this.getById(dictionaryName, id);
            return byId.orElseGet(() -> fallback.apply(null));
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getById', название = {}, id = {}",
                    dictionaryName, id, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Находит запись справочника по ID или возвращает значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param id уникальный идентификатор
     * @param defaultValue поставщик значения по умолчанию
     * @return найденная запись или значение по умолчанию
     */
    default <T> T getByIdOrDefault(String dictionaryName, int id, Supplier<T> defaultValue) {
        return this.getByIdWithFallback(dictionaryName, id, _ -> defaultValue.get());
    }

    /**
     * Находит запись справочника по коду и типу класса.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param code код записи
     * @return найденная запись или пустой Optional
     */
    <T> Optional<T> getByCode(Class<T> dictionaryType, String code);

    /**
     * Находит запись справочника по коду и названию.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param code код записи
     * @return найденная запись или пустой Optional
     */
    <T> Optional<T> getByCode(String dictionaryName, String code);

    /**
     * Находит запись справочника по коду с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param code код записи
     * @param fallback функция обработки ошибок
     * @return найденная запись или результат fallback функции
     */
    default <T> T getByCodeWithFallback(Class<T> dictionaryType, String code, Function<Exception, T> fallback) {
        try {
            Optional<T> byCode = this.getByCode(dictionaryType, code);
            return byCode.orElseGet(() -> fallback.apply(null));
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getByCode', тип = {}, код = {}",
                    dictionaryType, code, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Находит запись справочника по коду или возвращает значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param code код записи
     * @param defaultValue поставщик значения по умолчанию
     * @return найденная запись или значение по умолчанию
     */
    default <T> T getByCodeOrDefault(Class<T> dictionaryType, String code, Supplier<T> defaultValue) {
        return this.getByCodeWithFallback(dictionaryType, code, _ -> defaultValue.get());
    }

    /**
     * Находит запись справочника по коду с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param code код записи
     * @param fallback функция обработки ошибок
     * @return найденная запись или результат fallback функции
     */
    default <T> T getByCodeWithFallback(String dictionaryName, String code, Function<Exception, T> fallback) {
        try {
            Optional<T> byCode = this.getByCode(dictionaryName, code);
            return byCode.orElseGet(() -> fallback.apply(null));
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getByCode', название = {}, код = {}",
                    dictionaryName, code, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Находит запись справочника по коду или возвращает значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param code код записи
     * @param defaultValue поставщик значения по умолчанию
     * @return найденная запись или значение по умолчанию
     */
    default <T> T getByCodeOrDefault(String dictionaryName, String code, Supplier<T> defaultValue) {
        return this.getByCodeWithFallback(dictionaryName, code, _ -> defaultValue.get());
    }

    /**
     * Находит запись справочника по наименованию и типу класса.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param name наименование записи
     * @return найденная запись или пустой Optional
     */
    <T> Optional<T> getByName(Class<T> dictionaryType, String name);

    /**
     * Находит запись справочника по наименованию и названию.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param name наименование записи
     * @return найденная запись или пустой Optional
     */
    <T> Optional<T> getByName(String dictionaryName, String name);

    /**
     * Находит запись справочника по наименованию с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param name наименование записи
     * @param fallback функция обработки ошибок
     * @return найденная запись или результат fallback функции
     */
    default <T> T getByNameWithFallback(Class<T> dictionaryType, String name, Function<Exception, T> fallback) {
        try {
            Optional<T> byName = this.getByName(dictionaryType, name);
            return byName.orElseGet(() -> fallback.apply(null));
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getByName', тип = {}, наименование = {}",
                    dictionaryType, name, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Находит запись справочника по наименованию или возвращает значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryType класс сущности справочника
     * @param name наименование записи
     * @param defaultValue поставщик значения по умолчанию
     * @return найденная запись или значение по умолчанию
     */
    default <T> T getByNameOrDefault(Class<T> dictionaryType, String name, Supplier<T> defaultValue) {
        return this.getByNameWithFallback(dictionaryType, name, _ -> defaultValue.get());
    }

    /**
     * Находит запись справочника по наименованию с обработкой ошибок через fallback.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param name наименование записи
     * @param fallback функция обработки ошибок
     * @return найденная запись или результат fallback функции
     */
    default <T> T getByNameWithFallback(String dictionaryName, String name, Function<Exception, T> fallback) {
        try {
            Optional<T> byName = this.getByCode(dictionaryName, name);
            return byName.orElseGet(() -> fallback.apply(null));
        } catch (Exception dictionaryAccessException) {
            getLogger().warn(
                    "Ошибка получения справочных данных: метод = 'getByName', название = {}, наименование = {}",
                    dictionaryName, name, dictionaryAccessException
            );
            return fallback.apply(dictionaryAccessException);
        }
    }

    /**
     * Находит запись справочника по наименованию или возвращает значение по умолчанию при ошибке.
     *
     * @param <T> тип сущности справочника
     * @param dictionaryName название справочника
     * @param name наименование записи
     * @param defaultValue поставщик значения по умолчанию
     * @return найденная запись или значение по умолчанию
     */
    default <T> T getByNameOrDefault(String dictionaryName, String name, Supplier<T> defaultValue) {
        return this.getByNameWithFallback(dictionaryName, name, _ -> defaultValue.get());
    }

    /**
     * Возвращает логгер для записи информации об операциях со справочниками.
     *
     * @return логгер для записи операций
     */
    Logger getLogger();

    /**
     * A constant representing an undefined or uninitialized ID value.
     * This is typically used as a sentinel value to denote that an ID has
     * not been set or is in an invalid state.
     */
    public static final Integer UNDEFINED_ID = Integer.MIN_VALUE;
}
