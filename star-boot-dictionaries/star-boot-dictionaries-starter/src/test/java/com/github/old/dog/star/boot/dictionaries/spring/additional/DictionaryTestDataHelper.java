package com.github.old.dog.star.boot.dictionaries.spring.additional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Утилитный класс для подготовки тестовых данных в PostgreSQL.
 */
@Slf4j
@UtilityClass
public class DictionaryTestDataHelper {

    /**
     * Получает количество записей в тестовой таблице.
     */
    public static int getTestDataCount(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*)::int FROM test.categories", Integer.class);
    }

    /**
     * Получает информацию о PostgreSQL контейнере для логирования.
     */
    public static void logContainerInfo(PostgreSQLContainer<?> container) {
        if (container.isRunning()) {
            log.info("PostgreSQL контейнер запущен:");
            log.info("  - JDBC URL: {}", container.getJdbcUrl());
            log.info("  - Username: {}", container.getUsername());
            log.info("  - Database: {}", container.getDatabaseName());
            log.info("  - Container ID: {}", container.getContainerId());
            log.info("  - Mapped Port: {}", container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT));
        }
    }


    /**
     * Создает мок данные для unit тестов (совместимые с PostgreSQL).
     */
    public static List<Map<String, Object>> createMockRowData() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        return List.of(
                Map.of(
                        "id", 1,
                        "code", "TECH",
                        "name", "Technology",
                        "definition", "Technology sector companies",
                        "is_active", true,
                        "sort_order", 1,
                        "creation_timestamp", now
                ),
                Map.of(
                        "id", 2,
                        "code", "FINANCE",
                        "name", "Finance",
                        "definition", "Financial services sector",
                        "is_active", true,
                        "sort_order", 2,
                        "creation_timestamp", now.plusMinutes(1)
                ),
                Map.of(
                        "id", 3,
                        "code", "HEALTHCARE",
                        "name", "Healthcare",
                        "definition", "Healthcare and pharmaceuticals",
                        "is_active", false,
                        "sort_order", 3,
                        "creation_timestamp", now.plusMinutes(2)
                )
        );
    }

    /**
     * Выполняет произвольный SQL запрос для отладки.
     */
    public static void executeDebugQuery(JdbcTemplate jdbcTemplate, String sql) {
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            log.debug("Результат отладочного запроса '{}': {} строк", sql, results.size());
            results.forEach(row -> log.debug("  {}", row));
        } catch (Exception e) {
            log.debug("Ошибка выполнения отладочного запроса '{}': {}", sql, e.getMessage());
        }
    }
}
