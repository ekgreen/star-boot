
package com.github.old.dog.star.boot.dictionaries.engines.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryAttributeMapper;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryEngine;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryRow;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryRowMapper;
import com.github.old.dog.star.boot.dictionaries.api.throwbles.DictionaryAccessException;


/**
 * JDBC реализация движка для работы со справочными данными.
 *
 * <p>Использует Spring JdbcTemplate для выполнения SQL запросов к реляционным базам данных.
 * Обеспечивает стандартные операции CRUD для словарных данных с поддержкой поиска
 * по ID, коду и наименованию.</p>
 *
 * <p><strong>Особенности реализации:</strong></p>
 * <ul>
 *   <li>Автоматическое обнаружение структуры таблицы через ResultSetMetaData</li>
 *   <li>Регистронезависимый поиск по коду и наименованию</li>
 *   <li>Унифицированное логирование с проверкой уровня</li>
 *   <li>Преобразование SQLException в DataAccessException иерархию</li>
 *   <li>Поддержка стандартных имен колонок: id, code, name</li>
 * </ul>
 *
 * <p><strong>Требования к таблицам:</strong></p>
 * <ul>
 *   <li>Наличие колонки 'id' для уникальной идентификации</li>
 *   <li>Наличие колонки 'code' для поиска по коду (опционально)</li>
 *   <li>Наличие колонки 'name' для поиска по наименованию (опционально)</li>
 * </ul>
 *
 * @author AI Assistant
 * @see DictionaryEngine Базовый интерфейс движка
 * @see JdbcTemplate Spring JDBC шаблон
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JdbcTemplateDictionaryEngine implements DictionaryEngine {

    private static final String OPERATION_GET_ALL = "getAll";
    private static final String OPERATION_GET_BY_ID = "getById";
    private static final String OPERATION_GET_BY_CODE = "getByCode";
    private static final String OPERATION_GET_BY_NAME = "getByName";

    /**
     * Имя таблицы в формате [schema.]table_name.
     */
    private final String tableName;

    /**
     * Spring JDBC Template для выполнения запросов.
     */
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<DictionaryRow> getAll() {
        return connect(
            JdbcTemplateDictionaryEngine.OPERATION_GET_ALL,
            this.buildSelectAllQuery(),
            null
        );
    }

    @Override
    public Optional<DictionaryRow> getById(int id) {
        return one(
            JdbcTemplateDictionaryEngine.OPERATION_GET_BY_ID,
            this.buildSelectByIdQuery(),
            new Object[]{id}
        );
    }

    @Override
    public Optional<DictionaryRow> getByCode(String code) {
        return one(
            JdbcTemplateDictionaryEngine.OPERATION_GET_BY_CODE,
            this.buildSelectByCodeQuery(),
            new Object[]{code}
        );
    }

    @Override
    public Optional<DictionaryRow> getByName(String name) {
        return one(
            JdbcTemplateDictionaryEngine.OPERATION_GET_BY_NAME,
            this.buildSelectByNameQuery(),
            new Object[]{name}
        );
    }

    // ========== Вспомогательные методы построения запросов ==========

    private Optional<DictionaryRow> one(String operation, String query, Object[] parameters) {
        List<DictionaryRow> result = connect(operation, query, parameters);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    private List<DictionaryRow> connect(String operation, String query, Object[] parameters) {

        if (log.isDebugEnabled()) {
            log.debug("Начало операции {} для таблицы: {}", operation, tableName);
        }

        try {
            final List<DictionaryRow> rows;

            if (Objects.isNull(parameters) || parameters.length == 0) {
                if (log.isTraceEnabled()) {
                    log.trace("Выполнение SQL запроса: {}", query);
                }

                rows = jdbcTemplate.query(query, new JdbcDictionaryRowMapper());
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Выполнение SQL запроса: {} с параметрами {}", query, parameters);
                }
                rows = jdbcTemplate.query(query, new ArgumentPreparedStatementSetter(parameters), new JdbcDictionaryRowMapper());
            }

            log.info("Операция {} для таблицы '{}' выполнена успешно: загружено {} записей:",
                operation, tableName, rows.size());

            if (rows.isEmpty()) {
                log.warn("Справочник '{}' не содержит записей", tableName);
            } else if (log.isDebugEnabled() && rows.getFirst() instanceof JdbcDictionaryRow row) {
                log.debug("Структура записи таблицы '{}': атрибутов={}, колонки={}",
                    tableName, row.getAttributeCount(), row.getAttributeNames());
            }

            return rows;
        } catch (Exception e) {
            throw handleException(e, operation, "Ошибка при загрузке всех записей");
        }
    }

    /**
     * Строит запрос для получения всех записей.
     */
    private String buildSelectAllQuery() {
        return "SELECT * FROM " + tableName + " ORDER BY id";
    }

    /**
     * Строит запрос для поиска по ID.
     */
    private String buildSelectByIdQuery() {
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }

    /**
     * Строит запрос для поиска по коду (регистронезависимый).
     */
    private String buildSelectByCodeQuery() {
        return "SELECT * FROM " + tableName + " WHERE UPPER(TRIM(code)) = UPPER(TRIM(?))";
    }

    /**
     * Строит запрос для поиска по наименованию (регистронезависимый).
     */
    private String buildSelectByNameQuery() {
        return "SELECT * FROM " + tableName + " WHERE UPPER(TRIM(name)) = UPPER(TRIM(?))";
    }

    // ========== Методы валидации параметров ==========


    // ========== Обработка исключений ==========

    /**
     * Обрабатывает исключения и преобразует их в иерархию DataAccessException.
     */
    private DictionaryAccessException handleException(Exception dataAccessException, String operation, String message) {
        final String contextMessage = String.format("Таблица: %s, Операция: %s, Ошибка: %s", tableName, operation, message);

        log.error("{}: {}", message, dataAccessException.getMessage(), dataAccessException);
        return new DictionaryAccessException(contextMessage, DictionaryAccessException.AccessType.READ, dataAccessException);

    }

    // ========== Внутренние классы ==========

    /**
     * RowMapper для преобразования ResultSet в DictionaryRow.
     */
    private static class JdbcDictionaryRowMapper implements RowMapper<DictionaryRow> {

        @Override
        public DictionaryRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            final JdbcDictionaryRow.Builder builder = JdbcDictionaryRow.builder();

            final ResultSetMetaData metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) { // JDBC колонки начинаются с 1
                final String columnName = metaData.getColumnName(i).toLowerCase(); // Нормализуем к нижнему регистру
                final Object value = rs.getObject(i);
                final int columnType = metaData.getColumnType(i);

                builder.addAttribute(columnName, value, columnType);
            }

            return builder.build();
        }
    }

    /**
     * Реализация DictionaryRow для JDBC данных.
     */
    @RequiredArgsConstructor
    private static class JdbcDictionaryRow implements DictionaryRow {

        private final Map<String, Integer> attributes;
        private final Map<Integer, Object> values;
        private final Map<Integer, Integer> types;

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public int getId() {
            return Objects.requireNonNull(getAttribute("id"), "id cannot be null");
        }

        @Override
        public String getCode() {
            return getAttribute("code");
        }

        @Override
        public String getName() {
            return getAttribute("name");
        }

        @Override
        public String getDefinition() {
            return getAttribute("definition");
        }

        @Override
        public java.time.LocalDateTime getCreationTimestamp() {
            return getAttribute("creation_timestamp");
        }

        @Override
        public <T> T attribute(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Имя атрибута не может быть null");
            }

            final Integer index = attributes.get(name.toLowerCase());
            if (index == null) {
                throw new RuntimeException("Атрибут '" + name + "' не найден");
            }

            try {
                @SuppressWarnings("unchecked")
                T result = (T) values.get(index);
                return result;
            } catch (ClassCastException e) {
                throw new RuntimeException("Невозможно преобразовать атрибут '" + name + "' к запрашиваемому типу", e);
            }
        }

        @Override
        public int getAttributeType(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Имя атрибута не может быть null");
            }

            final Integer index = attributes.get(name.toLowerCase());
            if (index == null) {
                throw new RuntimeException("Атрибут '" + name + "' не найден");
            }

            return types.get(index);
        }

        @Override
        public <T> T attribute(String name, DictionaryAttributeMapper<T> mapper) {
            if (name == null) {
                throw new IllegalArgumentException("Имя атрибута не может быть null");
            }
            if (mapper == null) {
                throw new IllegalArgumentException("Маппер не может быть null");
            }

            final Integer index = attributes.get(name.toLowerCase());
            if (index == null) {
                throw new RuntimeException("Атрибут '" + name + "' не найден");
            }

            try {
                final Object value = values.get(index);
                final int type = types.get(index);
                return mapper.mapAttribute(value, type);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка преобразования атрибута '" + name + "' с помощью маппера", e);
            }
        }

        @Override
        public <T> T cast(DictionaryRowMapper<T> rowMapper) {
            if (rowMapper == null) {
                throw new IllegalArgumentException("Маппер строки не может быть null");
            }

            try {
                return rowMapper.mapRow(this);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка преобразования строки словаря с помощью маппера", e);
            }
        }

        @Override
        public String getDriver() {
            return "jdbc";
        }

        /**
         * Получает атрибут с приведением типа.
         */
        private <T> T getAttribute(String name) {
            try {
                return attribute(name);
            } catch (Exception e) {
                return null; // Возвращаем null для отсутствующих атрибутов
            }
        }

        /**
         * Проверяет существование атрибута.
         */
        public boolean hasAttribute(String attributeName) {
            return attributeName != null && attributes.containsKey(attributeName.toLowerCase());
        }

        /**
         * Возвращает все имена атрибутов.
         */
        public Set<String> getAttributeNames() {
            return Set.copyOf(attributes.keySet());
        }

        /**
         * Возвращает количество атрибутов.
         */
        public int getAttributeCount() {
            return attributes.size();
        }

        /**
         * Builder для создания JdbcDictionaryRowImpl.
         */
        public static class Builder {
            private final Map<String, Integer> attributes = new HashMap<>();
            private final Map<Integer, Object> values = new HashMap<>();
            private final Map<Integer, Integer> types = new HashMap<>();
            private int index = 0;

            public Builder addAttribute(String columnName, Object value, int type) {
                if (columnName == null) {
                    throw new IllegalArgumentException("Имя колонки не может быть null");
                }

                this.attributes.put(columnName.toLowerCase(), index); // Нормализуем к нижнему регистру
                this.values.put(index, value);
                this.types.put(index, type);
                this.index++;

                return this;
            }

            public DictionaryRow build() {
                if (attributes.isEmpty()) {
                    throw new IllegalStateException("Строка словаря должна содержать хотя бы один атрибут");
                }

                return new JdbcDictionaryRow(
                    Map.copyOf(attributes),
                    values,
                    Map.copyOf(types)
                );
            }
        }
    }
}
