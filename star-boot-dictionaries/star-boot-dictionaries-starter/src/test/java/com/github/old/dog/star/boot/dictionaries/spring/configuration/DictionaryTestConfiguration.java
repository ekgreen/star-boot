package com.github.old.dog.star.boot.dictionaries.spring.configuration;

import javax.sql.DataSource;
import com.github.old.dog.star.boot.dictionaries.annotation.EnableDictionaries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Тестовая конфигурация для проверки системы справочников с PostgreSQL Testcontainers.
 *
 * <p>Создает изолированную тестовую среду с реальным PostgreSQL контейнером,
 * что обеспечивает максимальную близость к production окружению.</p>
 */
@Slf4j
@TestConfiguration
@EnableAutoConfiguration
@EnableTransactionManagement  // Если нужны транзакции
@EnableDictionaries(
        basePackages = "com.github.old.dog.star.boot.dictionaries.spring.entities",
        dictionaryFactoryBeanName = "jdbcDictionaryFactory",
        enableDebugLogging = true
)
public class DictionaryTestConfiguration {

    /**
     * Создает и настраивает PostgreSQL контейнер для тестов.
     *
     * <p>Использует официальный образ PostgreSQL с настройками,
     * оптимизированными для тестирования.</p>
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer<?> postgresContainer() {
        // todo почитать
        log.info("Инициализация PostgreSQL Testcontainer");

        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:15-alpine")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("dictionary_test")
                .withUsername("test_user")
                .withPassword("test_password")
                .withInitScript("scripts/jdbc/01--test-case--dictionaries-with-categories.sql")  // Скрипт инициализации
                .withReuse(false);  // Для изолированности тестов

        // Настройки производительности для тестов
        container.withEnv("POSTGRES_INITDB_ARGS", "--auth-host=trust");
        container.withCommand(
                "postgres",
                "-c", "fsync=off",           // Отключаем fsync для скорости
                "-c", "synchronous_commit=off", // Асинхронные коммиты
                "-c", "full_page_writes=off",   // Отключаем full page writes
                "-c", "wal_buffers=16MB",       // Увеличиваем WAL буферы
                "-c", "shared_buffers=256MB"    // Увеличиваем shared buffers
        );

        log.info("PostgreSQL Testcontainer настроен: образ={}, БД={}",
                container.getDockerImageName(), container.getDatabaseName());

        return container;
    }

    /**
     * Создает DataSource на основе запущенного PostgreSQL контейнера.
     */
    @Bean
    @Primary
    @DependsOn("postgresContainer")
    public DataSource testDataSource(PostgreSQLContainer<?> postgresContainer) {
        log.info("Создание DataSource для PostgreSQL контейнера");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(postgresContainer.getDriverClassName());
        dataSource.setUrl(postgresContainer.getJdbcUrl());
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());

        log.info("DataSource создан: URL={}, пользователь={}",
                postgresContainer.getJdbcUrl(), postgresContainer.getUsername());

        return dataSource;
    }

    /**
     * Создает JdbcTemplate для работы с тестовой базой данных.
     */
    @Bean
    @Primary
    public JdbcTemplate testJdbcTemplate(DataSource testDataSource) {
        log.debug("Создание JdbcTemplate для тестов");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(testDataSource);

        // Настройки для тестов
        jdbcTemplate.setQueryTimeout(30); // 30 секунд таймаут
        jdbcTemplate.setFetchSize(100);    // Размер выборки

        return jdbcTemplate;
    }

    @Bean
    @Primary
    public TransactionTemplate testTransactionalTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
