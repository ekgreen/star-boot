package com.github.old.dog.star.boot.dictionaries.api;

import com.github.old.dog.star.boot.dictionaries.api.annotations.Code;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryTable;
import com.github.old.dog.star.boot.dictionaries.api.annotations.Id;
import com.github.old.dog.star.boot.dictionaries.api.annotations.Name;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAccessException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Интерфейс для работы со справочными данными.
 *
 * <p>Представляет универсальный контракт для доступа к справочникам различных типов.
 * Обеспечивает стандартизированный способ получения данных из справочников с поддержкой
 * поиска по основным атрибутам (ID, код, наименование).</p>
 *
 * <p><strong>Основные возможности:</strong></p>
 * <ul>
 *   <li>Получение полного списка записей словаря</li>
 *   <li>Поиск записи по уникальному идентификатору</li>
 *   <li>Поиск записи по коду</li>
 *   <li>Поиск записи по наименованию</li>
 *   <li>Получение метаинформации о структуре словаря</li>
 * </ul>
 *
 * <p><strong>Пример использования:</strong></p>
 * <pre>{@code
 * @Autowired
 * private Dictionary<SubjectSectorEntity> sectorDictionary;
 *
 * // Получение всех секторов
 * List<SubjectSectorEntity> allSectors = sectorDictionary.getAll();
 *
 * // Поиск сектора по коду
 * Optional<SubjectSectorEntity> bankSector = sectorDictionary.getByCode("BANK");
 *
 * // Поиск сектора по наименованию
 * Optional<SubjectSectorEntity> sector = sectorDictionary.getByName("Банковский сектор");
 *
 * // Получение метаинформации
 * String dictionaryName = sectorDictionary.getDictionaryName();
 * Set<String> attributes = sectorDictionary.getAttributesName();
 * }</pre>
 *
 * <p><strong>Особенности реализации:</strong></p>
 * <ul>
 *   <li>Все методы поиска возвращают {@link Optional} для безопасной обработки отсутствующих данных</li>
 *   <li>Интерфейс является generic, что обеспечивает типобезопасность</li>
 *   <li>Реализации должны поддерживать кэширование для оптимизации производительности</li>
 *   <li>Поиск по коду и наименованию должен быть регистронезависимым</li>
 * </ul>
 *
 * <p><strong>Интеграция с аннотациями:</strong></p>
 * <p>Сущности справочников должны быть аннотированы специальными аннотациями:</p>
 * <ul>
 *   <li>{@code @DictionaryTable} - указывает схему и таблицу в БД</li>
 *   <li>{@code @Id} - помечает поле идентификатора</li>
 *   <li>{@code @Code} - помечает поле кода</li>
 *   <li>{@code @Name} - помечает поле наименования</li>
 *   <li>{@code @Definition} - помечает поле описания</li>
 *   <li>{@code @CreationTimestamp} - помечает поле времени создания</li>
 * </ul>
 *
 * @param <T> тип сущности словаря, должен реализовывать интерфейс {@code DictionaryRow}
 *           или быть аннотирован соответствующими аннотациями
 *
 * @author AI Assistant
 * @since 1.0.0
 * @see DictionaryTable
 * @see Id
 * @see Code
 * @see Name
 */
public interface Dictionary<T> {

    /**
     * Возвращает тип сущности справочника.
     *
     * <p>Возвращает класс объекта, который используется для представления
     * записей данного справочника. Этот метод полезен для динамической
     * работы с типами справочников в runtime.</p>
     *
     * @return класс типа сущности справочника
     * @throws IllegalStateException если тип не может быть определен
     */
    Class<T> getDictionaryType();


    /**
     * Возвращает имя словаря.
     *
     * <p>Имя формируется на основе аннотации {@code @DictionaryTable}
     * в формате "schema.table" или только "table", если схема не указана.</p>
     *
     * <p><strong>Пример:</strong></p>
     * <pre>{@code
     * String name = sectorDictionary.getDictionaryName();
     * // Результат: "ratings.dict_subject_sector"
     * }</pre>
     *
     * @return имя словаря в формате "schema.table" или "table"
     * @throws IllegalStateException если не удалось определить имя словаря
     */
    String getDictionaryName();

    /**
     * Возвращает набор имен атрибутов (полей) словаря.
     *
     * <p>Включает все поля сущности, которые маппятся на колонки таблицы БД.
     * Имена атрибутов соответствуют именам колонок в базе данных.</p>
     *
     * <p><strong>Пример:</strong></p>
     * <pre>{@code
     * Set<String> attributes = sectorDictionary.getAttributesName();
     * // Результат: ["id", "code", "name", "definition", "creation_timestamp"]
     * }</pre>
     *
     * @return неизменяемый набор имен атрибутов словаря
     * @throws IllegalStateException если не удалось определить структуру словаря
     */
    Set<String> getAttributesName();

    /**
     * Возвращает все записи словаря.
     *
     * <p>Загружает полный список записей из базы данных.
     * Результат может кэшироваться для повышения производительности.</p>
     *
     * <p><strong>Особенности:</strong></p>
     * <ul>
     *   <li>Записи возвращаются в порядке, определенном в БД (обычно по ID)</li>
     *   <li>Если словарь пуст, возвращается пустой список</li>
     *   <li>Список является неизменяемым</li>
     * </ul>
     *
     * <p><strong>Пример:</strong></p>
     * <pre>{@code
     * List<SubjectSectorEntity> allSectors = sectorDictionary.getAll();
     * allSectors.forEach(sector -> {
     *     log.info("Сектор: {} - {}", sector.getCode(), sector.getName());
     * });
     * }</pre>
     *
     * @return список всех записей словаря, никогда не {@code null}
     * @throws DictionaryAccessException если произошла ошибка при обращении к БД
     */
    List<T> getAll();

    /**
     * Находит запись словаря по уникальному идентификатору.
     *
     * <p>Выполняет поиск записи по значению поля, помеченного аннотацией {@code @Id}.
     * Поиск выполняется с использованием точного соответствия.</p>
     *
     * <p><strong>Примечание:</strong> В текущей версии метод не принимает параметры.
     * Предполагается, что реализация будет дополнена параметром ID в будущих версиях.</p>
     *
     * <p><strong>Пример использования (планируемый):</strong></p>
     * <pre>{@code
     * Optional<SubjectSectorEntity> sector = sectorDictionary.getById(1);
     * if (sector.isPresent()) {
     *     System.out.println("Найден сектор: " + sector.get().getName());
     * }
     * }</pre>
     *
     * @param id уникальный идентификатор записи
     * @return {@link Optional} содержащий найденную запись или пустой, если запись не найдена
     * @throws DictionaryAccessException если произошла ошибка при обращении к БД
     * @throws IllegalArgumentException если передан недопустимый идентификатор
     *
     * @apiNote Сигнатура метода будет изменена в будущих версиях для принятия параметра ID
     */
    Optional<T> getById(int id);

    /**
     * Находит запись словаря по коду.
     *
     * <p>Выполняет поиск записи по значению поля, помеченного аннотацией {@code @Code}.
     * Поиск должен быть регистронезависимым для удобства использования.</p>
     *
     * <p><strong>Примечание:</strong> В текущей версии метод не принимает параметры.
     * Предполагается, что реализация будет дополнена параметром кода в будущих версиях.</p>
     *
     * <p><strong>Пример использования (планируемый):</strong></p>
     * <pre>{@code
     * Optional<SubjectSectorEntity> sector = sectorDictionary.getByCode("BANK");
     * sector.ifPresent(s -> {
     *     log.info("Банковский сектор: {}", s.getName());
     * });
     * }</pre>
     *
     * @param code код записи (регистронезависимый поиск)
     * @return {@link Optional} содержащий найденную запись или пустой, если запись не найдена
     * @throws DictionaryAccessException если произошла ошибка при обращении к БД
     * @throws IllegalArgumentException если передан пустой или null код
     *
     * @apiNote Сигнатура метода будет изменена в будущих версиях для принятия параметра кода
     */
    Optional<T> getByCode(String code);

    /**
     * Находит запись словаря по наименованию.
     *
     * <p>Выполняет поиск записи по значению поля, помеченного аннотацией {@code @Name}.
     * Поиск должен быть регистронезависимым и поддерживать частичное совпадение.</p>
     *
     * <p><strong>Особенности поиска:</strong></p>
     * <ul>
     *   <li>Регистронезависимый поиск</li>
     *   <li>Точное соответствие по умолчанию</li>
     *   <li>Возможность настройки частичного поиска в реализациях</li>
     * </ul>
     *
     * <p><strong>Примечание:</strong> В текущей версии метод не принимает параметры.
     * Предполагается, что реализация будет дополнена параметром наименования в будущих версиях.</p>
     *
     * <p><strong>Пример использования (планируемый):</strong></p>
     * <pre>{@code
     * Optional<SubjectSectorEntity> sector = sectorDictionary.getByName("Банковский сектор");
     * if (sector.isPresent()) {
     *     log.info("Найден сектор с кодом: {}", sector.get().getCode());
     * } else {
     *     log.warn("Сектор с наименованием 'Банковский сектор' не найден");
     * }
     * }</pre>
     *
     * @param name наименование записи (регистронезависимый поиск)
     * @return {@link Optional} содержащий найденную запись или пустой
     * @throws DictionaryAccessException если произошла ошибка при обращении к БД
     * @throws IllegalArgumentException если передано пустое или null наименование
     */
    Optional<T> getByName(String name);
}
