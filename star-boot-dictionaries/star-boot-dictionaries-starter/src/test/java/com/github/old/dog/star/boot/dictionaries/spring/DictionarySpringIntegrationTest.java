package com.github.old.dog.star.boot.dictionaries.spring;

import java.time.Duration;
import java.util.List;
import com.github.old.dog.star.boot.dictionaries.api.Dictionary;
import com.github.old.dog.star.boot.dictionaries.spring.configuration.DictionaryTestConfiguration;
import com.github.old.dog.star.boot.dictionaries.spring.entities.TestCategoryEntity;
import com.github.old.dog.star.boot.dictionaries.spring.additional.DictionaryTestDataHelper;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Интеграционный тест системы справочников с PostgreSQL Testcontainers.
 *
 * <p>Использует реальный PostgreSQL контейнер для максимальной
 * близости к production окружению.</p>
 */
@Slf4j
@SpringJUnitConfig(classes = DictionaryTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Интеграционный тест системы справочников с PostgreSQL Testcontainers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DictionarySpringIntegrationTest {

    @Autowired
    private PostgreSQLContainer<?> postgresContainer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private Dictionary<TestCategoryEntity> testCategoryDictionary;

    @Test
    @Order(1)
    @DisplayName("Проверка PostgreSQL Testcontainer инфраструктуры")
    void shouldHavePostgreSQLTestInfrastructure() {
        log.info("=== Тест 1: Проверка PostgreSQL Testcontainer ===");

        // Проверяем контейнер
        assertThat(postgresContainer.isRunning())
                .as("PostgreSQL контейнер должен быть запущен")
                .isTrue();

        assertThat(postgresContainer.getDatabaseName())
                .as("Имя базы данных")
                .isEqualTo("dictionary_test");

        // Проверяем подключение
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
        assertThat(version)
                .as("Версия PostgreSQL должна быть доступна")
                .isNotNull()
                .contains("PostgreSQL");

        // Проверяем данные
        int count = DictionaryTestDataHelper.getTestDataCount(jdbcTemplate);
        assertThat(count)
                .as("Тестовые данные должны быть загружены")
                .isGreaterThan(0);

        log.info("✓ PostgreSQL Testcontainer настроен: версия={}, записей={}",
                version.split(" ")[1], count);
    }

    @Test
    @Order(2)
    @DisplayName("Проверка загрузки данных из PostgreSQL")
    void shouldLoadDataFromPostgreSQL() {
        log.info("=== Тест 2: Загрузка данных из PostgreSQL ===");

        List<TestCategoryEntity> categories = testCategoryDictionary.getAll();

        assertThat(categories)
                .as("Данные должны быть загружены из PostgreSQL")
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(5);

        // Проверяем специфичные для PostgreSQL типы данных
        TestCategoryEntity techCategory = categories.stream()
                .filter(cat -> "TECH".equals(cat.getCode()))
                .findFirst()
                .orElseThrow();

        assertThat(techCategory.getCreationTimestamp())
                .as("PostgreSQL timestamp должен быть корректно преобразован")
                .isNotNull();

        assertThat(techCategory.getIsActive())
                .as("PostgreSQL boolean должен быть корректно преобразован")
                .isInstanceOf(Boolean.class)
                .isTrue();

        log.info("✓ Загружено {} записей из PostgreSQL, первая запись: {} ({})",
                categories.size(), techCategory.getName(), techCategory.getCreationTimestamp());
    }

    @Test
    @Order(3)
    @DisplayName("Проверка производительности с PostgreSQL")
    void shouldPerformWellWithPostgreSQL() {
        log.info("=== Тест 3: Производительность с PostgreSQL ===");

        // Замеряем время выполнения
        long startTime = System.currentTimeMillis();

        // Выполняем несколько операций
        for (int i = 0; i < 5; i++) {
            List<TestCategoryEntity> categories = testCategoryDictionary.getAll();
            assertThat(categories).hasSizeGreaterThan(0);

            // Добавляем запись
            jdbcTemplate.update("""
                        INSERT INTO test.categories (id, code, name, definition, is_active, sort_order)
                        VALUES (?, ?, ?, ?, ?, ?)
                    """, 100 + i, "PERF_" + i, "Performance Test " + i, "Test record", true, 100 + i);
        }

        long endTime = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(endTime - startTime);

        assertThat(duration)
                .as("Операции с PostgreSQL должны выполняться за разумное время")
                .isLessThan(Duration.ofSeconds(10));

        log.info("✓ 5 циклов операций выполнены за {} мс", duration.toMillis());
    }

    @Test
    @Order(4)
    @DisplayName("Проверка транзакционности PostgreSQL")
    void shouldSupportTransactions() {
        log.info("=== Тест 4: Транзакционность PostgreSQL ===");

        int initialCount = DictionaryTestDataHelper.getTestDataCount(jdbcTemplate);

        // Пытаемся выполнить операцию в транзакции
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jdbcTemplate.update("""
                                INSERT INTO test.categories (id, code, name, definition, is_active, sort_order)
                                VALUES (?, ?, ?, ?, ?, ?)""",
                        999, "TRANS_TEST", "Transaction Test", "Test transaction", true, 999);

                int countAfterInsert = DictionaryTestDataHelper.getTestDataCount(jdbcTemplate);
                assertThat(countAfterInsert).isEqualTo(initialCount + 1);

                status.setRollbackOnly();
            }
        });

        int countAfterRollback = DictionaryTestDataHelper.getTestDataCount(jdbcTemplate);
        assertThat(countAfterRollback)
                .as("После ROLLBACK количество записей должно вернуться к исходному")
                .isEqualTo(initialCount);

        log.info("✓ Транзакционность PostgreSQL работает корректно: {} → {} → {}",
                initialCount, initialCount + 1, initialCount);
    }

    @Test
    @Order(5)
    @DisplayName("Проверка PostgreSQL специфичных функций")
    void shouldSupportPostgreSQLFeatures() {
        log.info("=== Тест 5: PostgreSQL специфичные функции ===");

        // Проверяем работу с PostgreSQL функциями
        String currentTime = jdbcTemplate.queryForObject("SELECT NOW()::text", String.class);
        assertThat(currentTime).isNotNull();

        // Проверяем работу с массивами (если нужно в будущем)
        Integer maxId = jdbcTemplate.queryForObject(
                "SELECT MAX(id) FROM test.categories", Integer.class);
        assertThat(maxId).isGreaterThan(0);

        // Проверяем информацию о схеме
        Integer tableCount = jdbcTemplate.queryForObject("""
                    SELECT COUNT(*)::int FROM information_schema.tables
                    WHERE table_name = 'categories'
                """, Integer.class);
        assertThat(tableCount).isEqualTo(1);

        log.info("✓ PostgreSQL функции работают: текущее время определено, максимальный ID={}, таблица существует",
                maxId);
    }

    @Test
    @Order(6)
    @DisplayName("Проверка работы словаря с PostgreSQL типами данных")
    void shouldHandlePostgreSQLDataTypes() {
        log.info("=== Тест 6: PostgreSQL типы данных ===");

        // Загружаем данные
        List<TestCategoryEntity> categories = testCategoryDictionary.getAll();
        TestCategoryEntity category = categories.get(0);

        // Проверяем типы данных PostgreSQL
        assertThat(category.getId())
                .as("PostgreSQL INTEGER")
                .isInstanceOf(Integer.class);

        assertThat(category.getCode())
                .as("PostgreSQL VARCHAR")
                .isInstanceOf(String.class);

        assertThat(category.getIsActive())
                .as("PostgreSQL BOOLEAN")
                .isInstanceOf(Boolean.class);

        assertThat(category.getCreationTimestamp())
                .as("PostgreSQL TIMESTAMP")
                .isNotNull();

        log.info("✓ PostgreSQL типы данных корректно обрабатываются: " +
                 "id={} ({}), code={} ({}), active={} ({})",
                category.getId(), category.getId().getClass().getSimpleName(),
                category.getCode(), category.getCode().getClass().getSimpleName(),
                category.getIsActive(), category.getIsActive().getClass().getSimpleName());
    }
}
