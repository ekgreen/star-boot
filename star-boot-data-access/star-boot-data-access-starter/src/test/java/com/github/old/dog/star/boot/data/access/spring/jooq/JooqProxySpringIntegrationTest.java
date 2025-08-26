package com.github.old.dog.star.boot.data.access.spring.jooq;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import com.github.old.dog.star.boot.data.access.spring.jooq.additional.ProxyTestCreditObjectObjectRepository;
import com.github.old.dog.star.boot.data.access.spring.jooq.configuration.JooqRepositoryTestConfiguration;
import com.github.old.dog.star.boot.data.access.spring.jooq.generated.Tables;
import com.github.old.dog.star.boot.data.access.spring.jooq.generated.tables.pojos.CreditObjectPojo;

/**
 * Интеграционный тест системы JOOQ репозиториев с PostgreSQL Testcontainers.
 *
 * <p>Использует реальный PostgreSQL контейнер для максимальной
 * близости к production окружению и проверяет все аспекты работы
 * автоматически созданных JOOQ репозиториев с таблицей ratings.credit_object.</p>
 */
@Slf4j
@SpringJUnitConfig(classes = JooqRepositoryTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Интеграционный тест системы JOOQ репозиториев с PostgreSQL Testcontainers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIfSystemProperty(named = "skip.container.tests", matches = "true")
class JooqProxySpringIntegrationTest {

    @Autowired
    private PostgreSQLContainer<?> postgresContainer;

    @Autowired
    private DSLContext context;

    @Autowired
    private ProxyTestCreditObjectObjectRepository proxyJooqRepository;

    // Простая последовательность для генерации ID
    private final AtomicLong idSequence = new AtomicLong(1000L);

    @Test
    @Order(1)
    @DisplayName("Проверка PostgreSQL Testcontainer и JOOQ инфраструктуры")
    void shouldHavePostgreSQLAndJooqInfrastructure() {
        log.info("=== Тест 1: Проверка PostgreSQL Testcontainer и JOOQ ===");

        // Проверяем контейнер
        assertThat(postgresContainer.isRunning())
            .as("PostgreSQL контейнер должен быть запущен")
            .isTrue();

        assertThat(postgresContainer.getDatabaseName())
            .as("Имя базы данных")
            .isEqualTo("jooq_test");

        // Проверяем подключение через context
        String version = (String) context.fetchValue("SELECT version()");
        assertThat(version)
            .as("Версия PostgreSQL должна быть доступна")
            .isNotNull()
            .contains("PostgreSQL");

        // Проверяем существование схемы ratings через context
        Integer schemaCount = (Integer) context.fetchValue("SELECT COUNT(*)::int FROM information_schema.schemata WHERE schema_name = 'ratings'");
        assertThat(schemaCount).isEqualTo(1);

        // Проверяем существование таблицы credit_object через context
        Integer tableCount = (Integer) context.fetchValue("""
            SELECT COUNT(*)::int FROM information_schema.tables
            WHERE table_schema = 'ratings' AND table_name = 'credit_object'
            """);
        assertThat(tableCount).isEqualTo(1);

        log.info("✓ PostgreSQL Testcontainer и схема настроены: версия={}, таблица credit_object существует",
            version.split(" ")[1]);
    }

    @Test
    @Order(2)
    @DisplayName("Проверка простых DSL операций")
    void shouldExecuteSimpleDslOperations() {
        log.info("=== Тест 2: Простые DSL операции ===");

        Integer initialCount = proxyJooqRepository.simpleDslOperation(dsl ->
            dsl.selectCount()
                .from(Tables.CREDIT_OBJECT)
                .fetchOne(0, Integer.class));

        assertThat(initialCount)
            .as("Начальное количество записей должно быть определено")
            .isNotNull()
            .isGreaterThanOrEqualTo(0);

        // Добавляем запись через DSL
        Long newId = idSequence.getAndIncrement();
        LocalDateTime now = LocalDateTime.now();

        Integer insertResult = proxyJooqRepository.simpleDslOperation(dsl ->
            dsl.insertInto(Tables.CREDIT_OBJECT)
                .columns(
                    Tables.CREDIT_OBJECT.ID,
                    Tables.CREDIT_OBJECT.CODE,
                    Tables.CREDIT_OBJECT.NAME,
                    Tables.CREDIT_OBJECT.SECTOR_ID,
                    Tables.CREDIT_OBJECT.IS_DELETED,
                    Tables.CREDIT_OBJECT.CREATION_TIMESTAMP,
                    Tables.CREDIT_OBJECT.MODIFICATION_TIMESTAMP
                )
                .values(newId, "TEST_DSL", "Test DSL Operation", 1, 0, now, now)
                .execute());

        assertThat(insertResult).isEqualTo(1);

        // Проверяем, что запись добавилась
        Integer finalCount = proxyJooqRepository.simpleDslOperation(dsl ->
            dsl.selectCount()
                .from(Tables.CREDIT_OBJECT)
                .fetchOne(0, Integer.class));

        assertThat(finalCount).isEqualTo(initialCount + 1);

        log.info("✓ DSL операции работают: {} → {} записей", initialCount, finalCount);
    }

    @Test
    @Order(3)
    @DisplayName("Проверка простых DAO операций")
    void shouldExecuteSimpleDaoOperations() {
        log.info("=== Тест 3: Простые DAO операции ===");

        int initialCount = getObjectCount();

        // Создаем тестовый объект
        CreditObjectPojo testObject = createTestObject(idSequence.incrementAndGet(), "TEST_DAO", "Test DAO Operation");

        // Вставляем через DAO
        proxyJooqRepository.simpleDaoOperation(dao -> {
            dao.insert(testObject);
            return null;
        });

        int finalCount = getObjectCount();
        assertThat(finalCount).isEqualTo(initialCount + 1);

        log.info("✓ DAO операции работают: {} → {} записей", initialCount, finalCount);
    }

    @Test
    @Order(4)
    @DisplayName("Проверка атомарных операций с транзакциями")
    void shouldExecuteAtomicOperationsWithTransactions() {
        log.info("=== Тест 4: Атомарные операции с транзакциями ===");

        int initialCount = getObjectCount();

        // Успешная атомарная операция
        proxyJooqRepository.atomicDslOperation(dsl -> {
            Long id1 = idSequence.getAndIncrement();
            Long id2 = idSequence.getAndIncrement();
            LocalDateTime now = LocalDateTime.now();

            dsl.insertInto(Tables.CREDIT_OBJECT)
                .columns(
                    Tables.CREDIT_OBJECT.ID,
                    Tables.CREDIT_OBJECT.CODE,
                    Tables.CREDIT_OBJECT.NAME,
                    Tables.CREDIT_OBJECT.SECTOR_ID,
                    Tables.CREDIT_OBJECT.IS_DELETED,
                    Tables.CREDIT_OBJECT.CREATION_TIMESTAMP,
                    Tables.CREDIT_OBJECT.MODIFICATION_TIMESTAMP
                )
                .values(id1, "ATOMIC_1", "Atomic Test 1", 1, 0, now, now)
                .execute();

            dsl.insertInto(Tables.CREDIT_OBJECT)
                .columns(
                    Tables.CREDIT_OBJECT.ID,
                    Tables.CREDIT_OBJECT.CODE,
                    Tables.CREDIT_OBJECT.NAME,
                    Tables.CREDIT_OBJECT.SECTOR_ID,
                    Tables.CREDIT_OBJECT.IS_DELETED,
                    Tables.CREDIT_OBJECT.CREATION_TIMESTAMP,
                    Tables.CREDIT_OBJECT.MODIFICATION_TIMESTAMP
                )
                .values(id2, "ATOMIC_2", "Atomic Test 2", 1, 0, now, now)
                .execute();

            return null;
        });

        int countAfterSuccess = getObjectCount();
        assertThat(countAfterSuccess).isEqualTo(initialCount + 2);

        log.info("✓ Атомарные операции работают: {} → {} записей", initialCount, countAfterSuccess);
    }

    @Test
    @Order(5)
    @DisplayName("Проверка template операций с дополнительной функциональностью")
    void shouldExecuteTemplateOperations() {
        log.info("=== Тест 5: Template операции ===");

        int initialCount = getObjectCount();

        // Используем template операции
        proxyJooqRepository.templateOperation(template -> {
            // Batch insert
            List<CreditObjectPojo> objectsToInsert = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Long id = template.generateNextId("tweety");
                objectsToInsert.add(createTestObject(id, "BATCH_" + i, "Batch Test " + i));
            }
            template.batchInsert(objectsToInsert);
            return null;
        });

        int finalCount = getObjectCount();
        assertThat(finalCount).isEqualTo(initialCount + 3);

        log.info("✓ Template операции работают: {} → {} записей", initialCount, finalCount);
    }

    @Test
    @Order(6)
    @DisplayName("Проверка производительности JOOQ операций")
    void shouldPerformWellWithJooq() {
        log.info("=== Тест 6: Производительность JOOQ ===");

        long startTime = System.currentTimeMillis();

        // Выполняем серию операций
        for (int i = 0; i < 10; i++) {
            Long id = idSequence.getAndIncrement();
            LocalDateTime now = LocalDateTime.now();

            final int index = i;

            proxyJooqRepository.simpleDslOperation(dsl ->
                dsl.insertInto(Tables.CREDIT_OBJECT)
                    .columns(
                        Tables.CREDIT_OBJECT.ID,
                        Tables.CREDIT_OBJECT.CODE,
                        Tables.CREDIT_OBJECT.NAME,
                        Tables.CREDIT_OBJECT.SECTOR_ID,
                        Tables.CREDIT_OBJECT.IS_DELETED,
                        Tables.CREDIT_OBJECT.CREATION_TIMESTAMP,
                        Tables.CREDIT_OBJECT.MODIFICATION_TIMESTAMP
                    )
                    .values(id, "PERF_" + index, "Performance Test " + index, 1, 0, now, now)
                    .execute());
        }

        long endTime = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(endTime - startTime);

        assertThat(duration)
            .as("10 операций JOOQ должны выполняться за разумное время")
            .isLessThan(Duration.ofSeconds(5));

        log.info("✓ 10 операций JOOQ выполнены за {} мс", duration.toMillis());
    }

    @Test
    @Order(7)
    @DisplayName("Проверка обработки ошибок и логирования")
    void shouldHandleErrorsAndLogging() {
        log.info("=== Тест 7: Обработка ошибок и логирование ===");

        // Проверяем обработку ошибок при нарушении уникальности
        Long duplicateId = idSequence.getAndIncrement();
        LocalDateTime now = LocalDateTime.now();

        // Первая вставка должна пройти успешно
        proxyJooqRepository.simpleDslOperation(dsl ->
            dsl.insertInto(Tables.CREDIT_OBJECT)
                .columns(
                    Tables.CREDIT_OBJECT.ID,
                    Tables.CREDIT_OBJECT.CODE,
                    Tables.CREDIT_OBJECT.NAME,
                    Tables.CREDIT_OBJECT.SECTOR_ID,
                    Tables.CREDIT_OBJECT.IS_DELETED,
                    Tables.CREDIT_OBJECT.CREATION_TIMESTAMP,
                    Tables.CREDIT_OBJECT.MODIFICATION_TIMESTAMP
                )
                .values(duplicateId, "ERROR_TEST", "Error Test", 1, 0, now, now)
                .execute());

        // Попытка вставить с тем же ID должна вызвать ошибку
        assertThatThrownBy(() ->
            proxyJooqRepository.simpleDslOperation(dsl ->
                dsl.insertInto(Tables.CREDIT_OBJECT)
                    .columns(
                        Tables.CREDIT_OBJECT.ID,
                        Tables.CREDIT_OBJECT.CODE,
                        Tables.CREDIT_OBJECT.NAME,
                        Tables.CREDIT_OBJECT.SECTOR_ID,
                        Tables.CREDIT_OBJECT.IS_DELETED,
                        Tables.CREDIT_OBJECT.CREATION_TIMESTAMP,
                        Tables.CREDIT_OBJECT.MODIFICATION_TIMESTAMP
                    )
                    .values(duplicateId, "ERROR_TEST_2", "Error Test 2", 1, 0, now, now)
                    .execute()))
            .as("Дублирование ID должно вызывать исключение")
            .isInstanceOf(Exception.class);

        log.info("✓ Обработка ошибок работает корректно");
    }

    // ================================================================================================
    // Helper Methods
    // ================================================================================================

    /**
     * Получает текущее количество кредитных объектов через context.
     */
    private Integer getObjectCount() {
        return (Integer) context.fetchValue("SELECT COUNT(*)::int FROM ratings.credit_object");
    }

    /**
     * Создает тестовый объект для операций.
     */
    private CreditObjectPojo createTestObject(Long id, String code, String name) {
        CreditObjectPojo object = new CreditObjectPojo();
        object.setId(id);
        object.setCode(code);
        object.setName(name);
        object.setSectorId(1);
        object.setIsDeleted(0);
        object.setCreationTimestamp(LocalDateTime.now());
        object.setModificationTimestamp(LocalDateTime.now());
        return object;
    }
}
