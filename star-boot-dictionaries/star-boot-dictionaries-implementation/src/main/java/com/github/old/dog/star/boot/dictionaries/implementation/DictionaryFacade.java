package com.github.old.dog.star.boot.dictionaries.implementation;

import com.github.old.dog.star.boot.dictionaries.api.Dictionaries;
import com.github.old.dog.star.boot.dictionaries.api.Dictionary;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Spring-реализация фасада для работы со справочниками.
 *
 * <p>Обеспечивает централизованный доступ ко всем зарегистрированным справочникам
 * через единый интерфейс {@link Dictionaries}. Поддерживает поиск справочников
 * как по типу класса, так и по названию.</p>
 *
 * <p><strong>Особенности реализации:</strong></p>
 * <ul>
 *   <li>Использует кэширование индексов для быстрого поиска справочников</li>
 *   <li>Потокобезопасная реализация</li>
 *   <li>Автоматическое логирование всех операций</li>
 *   <li>Валидация входных параметров</li>
 *   <li>Обработка ошибок с подробным логированием</li>
 * </ul>
 *
 * <p><strong>Инициализация:</strong></p>
 * <pre>{@code
 * @Configuration
 * public class DictionaryConfiguration {
 *
 *     @Bean
 *     public Dictionaries dictionaries(List<Dictionary<?>> dictionaries) {
 *         return new SpringDictionaryFacade(dictionaries);
 *     }
 * }
 * }</pre>
 *
 * @author AI Assistant
 * @see Dictionaries
 * @see Dictionary
 * @since 1.0.0
 */
@Slf4j
public class DictionaryFacade implements Dictionaries {

    private final List<Dictionary<?>> dictionaries;

    private final Map<Class<?>, Dictionary<?>> typeIndex = new HashMap<>();
    private final Map<String, Dictionary<?>> nameIndex = new HashMap<>();

    /**
     * Инициализирует фасад с переданным списком справочников.
     *
     * <p>Создает индексы для быстрого поиска справочников по типу и названию.
     * Выполняет валидацию всех справочников на предмет корректности настройки.</p>
     *
     * @param dictionaries список всех доступных справочников
     * @throws IllegalArgumentException если список содержит некорректные справочники
     */
    public DictionaryFacade(List<Dictionary<?>> dictionaries) {
        this.dictionaries = List.copyOf(dictionaries);
        initializeIndexes();
        log.info("Инициализирован SpringDictionaryFacade с {} справочниками", dictionaries.size());
    }

    private void initializeIndexes() {
        log.debug("Инициализация индексов справочников...");

        for (Dictionary<?> dictionary : dictionaries) {
            try {
                // Индексация по типу
                Class<?> dictionaryType = dictionary.getDictionaryType();
                if (typeIndex.containsKey(dictionaryType)) {
                    log.warn(
                            "Обнаружен дублирующийся справочник для типа {}: существующий = {}, новый = {}",
                            dictionaryType.getSimpleName(),
                            typeIndex.get(dictionaryType).getDictionaryName(),
                            dictionary.getDictionaryName()
                    );
                }
                typeIndex.put(dictionaryType, dictionary);

                // Индексация по названию
                String dictionaryName = dictionary.getDictionaryName();
                if (nameIndex.containsKey(dictionaryName)) {
                    log.warn(
                            "Обнаружен дублирующийся справочник с названием {}: существующий тип = {}, новый тип = {}",
                            dictionaryName,
                            nameIndex.get(dictionaryName).getDictionaryType().getSimpleName(),
                            dictionaryType.getSimpleName()
                    );
                }
                nameIndex.put(dictionaryName, dictionary);

                if (log.isDebugEnabled()) {
                    log.debug(
                            "Проиндексирован справочник: тип = {}, название = {}, атрибуты = {}",
                            dictionaryType.getSimpleName(),
                            dictionaryName,
                            dictionary.getAttributesName().size()
                    );
                }

            } catch (Exception e) {
                log.error(
                        "Ошибка при индексации справочника {}: {}",
                        dictionary.getClass().getSimpleName(), e.getMessage(), e
                );
                throw new IllegalArgumentException("Не удается проиндексировать справочник: " + e.getMessage(), e);
            }
        }

        log.info(
                "Индексы справочников инициализированы: по типу = {}, по названию = {}",
                typeIndex.size(), nameIndex.size()
        );

        if (log.isDebugEnabled()) {
            log.debug(
                    "Индекс по типам: {}",
                    typeIndex.keySet().stream()
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(", "))
            );
            log.debug("Индекс по названиям: {}", String.join(", ", nameIndex.keySet()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> dictionaryType) {
        validateDictionaryType(dictionaryType);

        Dictionary<?> dictionary = typeIndex.get(dictionaryType);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник для типа " + dictionaryType.getSimpleName() + " не найден"
            );
        }

        if (log.isDebugEnabled()) {
            log.debug("Получение всех записей справочника для типа {}", dictionaryType.getSimpleName());
        }

        try {
            return (List<T>) dictionary.getAll();
        } catch (Exception e) {
            log.error(
                    "Ошибка при получении всех записей справочника для типа {}: {}",
                    dictionaryType.getSimpleName(), e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось получить записи справочника для типа " + dictionaryType.getSimpleName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(String dictionaryName) {
        validateDictionaryName(dictionaryName);

        Dictionary<?> dictionary = nameIndex.get(dictionaryName);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник с названием '" + dictionaryName + "' не найден");
        }

        if (log.isDebugEnabled()) {
            log.debug("Получение всех записей справочника '{}'", dictionaryName);
        }

        try {
            return (List<T>) dictionary.getAll();
        } catch (Exception e) {
            log.error(
                    "Ошибка при получении всех записей справочника '{}': {}",
                    dictionaryName, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось получить записи справочника '" + dictionaryName + "'", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getById(Class<T> dictionaryType, int id) {
        validateDictionaryType(dictionaryType);
        validateId(id);

        Dictionary<?> dictionary = typeIndex.get(dictionaryType);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник для типа " + dictionaryType.getSimpleName() + " не найден");
        }

        if (log.isDebugEnabled()) {
            log.debug("Поиск записи справочника по ID: тип = {}, id = {}", dictionaryType.getSimpleName(), id);
        }

        try {
            return (Optional<T>) dictionary.getById(id);
        } catch (Exception e) {
            log.error(
                    "Ошибка при поиске записи справочника по ID: тип = {}, id = {}, ошибка = {}",
                    dictionaryType.getSimpleName(), id, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось найти запись справочника по ID для типа " + dictionaryType.getSimpleName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getById(String dictionaryName, int id) {
        validateDictionaryName(dictionaryName);
        validateId(id);

        Dictionary<?> dictionary = nameIndex.get(dictionaryName);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник с названием '" + dictionaryName + "' не найден");
        }

        log.debug("Поиск записи справочника по ID: название = '{}', id = {}", dictionaryName, id);

        try {
            return (Optional<T>) dictionary.getById(id);
        } catch (Exception e) {
            log.error(
                    "Ошибка при поиске записи справочника по ID: название = '{}', id = {}, ошибка = {}",
                    dictionaryName, id, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось найти запись справочника по ID для '" + dictionaryName + "'", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getByCode(Class<T> dictionaryType, String code) {
        validateDictionaryType(dictionaryType);
        validateCode(code);

        Dictionary<?> dictionary = typeIndex.get(dictionaryType);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник для типа " + dictionaryType.getSimpleName() + " не найден");
        }

        if (log.isDebugEnabled()) {
            log.debug("Поиск записи справочника по коду: тип = {}, код = '{}'", dictionaryType.getSimpleName(), code);
        }

        try {
            return (Optional<T>) dictionary.getByCode(code);
        } catch (Exception e) {
            log.error(
                    "Ошибка при поиске записи справочника по коду: тип = {}, код = '{}', ошибка = {}",
                    dictionaryType.getSimpleName(), code, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось найти запись справочника по коду для типа " + dictionaryType.getSimpleName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getByCode(String dictionaryName, String code) {
        validateDictionaryName(dictionaryName);
        validateCode(code);

        Dictionary<?> dictionary = nameIndex.get(dictionaryName);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник с названием '" + dictionaryName + "' не найден");
        }

        if (log.isDebugEnabled()) {
            log.debug("Поиск записи справочника по коду: название = '{}', код = '{}'", dictionaryName, code);
        }

        try {
            return (Optional<T>) dictionary.getByCode(code);
        } catch (Exception e) {
            log.error(
                    "Ошибка при поиске записи справочника по коду: название = '{}', код = '{}', ошибка = {}",
                    dictionaryName, code, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось найти запись справочника по коду для '" + dictionaryName + "'", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getByName(Class<T> dictionaryType, String name) {
        validateDictionaryType(dictionaryType);
        validateName(name);

        Dictionary<?> dictionary = typeIndex.get(dictionaryType);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник для типа " + dictionaryType.getSimpleName() + " не найден");
        }

        if (log.isDebugEnabled()) {
            log.debug(
                    "Поиск записи справочника по наименованию: тип = {}, наименование = '{}'",
                    dictionaryType.getSimpleName(), name
            );
        }


        try {
            return (Optional<T>) dictionary.getByName(name);
        } catch (Exception e) {
            log.error(
                    "Ошибка при поиске записи справочника по наименованию: тип = {}, наименование = '{}', ошибка = {}",
                    dictionaryType.getSimpleName(), name, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось найти запись справочника по наименованию для типа " + dictionaryType.getSimpleName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getByName(String dictionaryName, String name) {
        validateDictionaryName(dictionaryName);
        validateName(name);

        Dictionary<?> dictionary = nameIndex.get(dictionaryName);
        if (dictionary == null) {
            throw new RuntimeException(
                    "Справочник с названием '" + dictionaryName + "' не найден");
        }

        if (log.isDebugEnabled()) {
            log.debug(
                    "Поиск записи справочника по наименованию: название = '{}', наименование = '{}'",
                    dictionaryName, name
            );
        }

        try {
            return (Optional<T>) dictionary.getByName(name);
        } catch (Exception e) {
            log.error(
                    "Ошибка при поиске записи справочника по наименованию: название = '{}', наименование = '{}', ошибка = {}",
                    dictionaryName, name, e.getMessage(), e
            );
            throw new RuntimeException(
                    "Не удалось найти запись справочника по наименованию для '" + dictionaryName + "'", e);
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    private void validateDictionaryType(Class<?> dictionaryType) {
        if (dictionaryType == null) {
            throw new IllegalArgumentException("Тип справочника не может быть null");
        }
    }

    private void validateDictionaryName(String dictionaryName) {
        if (dictionaryName == null || dictionaryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название справочника не может быть null или пустым");
        }
    }

    private void validateId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
    }

    private void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Код записи не может быть null или пустым");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Наименование записи не может быть null или пустым");
        }
    }
}
