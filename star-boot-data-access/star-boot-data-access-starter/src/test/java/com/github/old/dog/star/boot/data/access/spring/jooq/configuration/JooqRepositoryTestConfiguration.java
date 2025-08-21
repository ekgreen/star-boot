package com.github.old.dog.star.boot.data.access.spring.jooq.configuration;

import com.github.old.dog.star.boot.data.access.annotation.EnableJooqRepositories;
import com.github.old.dog.star.boot.data.access.api.SequenceFactory;
import com.github.old.dog.star.boot.interfaces.Sequence;
import com.github.old.dog.star.boot.toolbox.collections.sequences.AtomicInfiniteLongSequence;
import com.github.old.dog.star.boot.toolbox.collections.sequences.InfiniteLongSequence;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Тестовая конфигурация для интеграционных тестов JOOQ репозиториев.
 *
 * <p>Настраивает:
 * <ul>
 *   <li>PostgreSQL Testcontainer</li>
 *   <li>JOOQ Configuration с PostgreSQL диалектом</li>
 *   <li>Автоматическую регистрацию JOOQ репозиториев</li>
 *   <li>Тестовые последовательности и утилиты</li>
 * </ul>
 */
@Slf4j
@EnableJooqRepositories(
    basePackages = {
        "com.github.old.dog.star.boot.data.access.spring.jooq"
    }
)
@ComponentScan(
    basePackages = {
        "com.github.old.dog.star.boot.data.access.spring.jooq"
    }
)
@TestConfiguration
public class JooqRepositoryTestConfiguration {

    /**
     * PostgreSQL контейнер для тестов.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Primary
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("jooq_test")
                .withUsername("test_user")
                .withPassword("test_password")
                .withInitScript("scripts/jooq/01--test-case--jooq-with-spring-integration.sql");

        log.info("Создан PostgreSQL контейнер для JOOQ тестов: {}", container.getDatabaseName());
        return container;
    }

    /**
     * Конфигурация DataSource для тестов с PostgreSQL Testcontainer.
     */
    @Bean
    @Primary
    public DataSource dataTestSource(PostgreSQLContainer<?> postgresContainer) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        config.setDriverClassName("org.postgresql.Driver");

        // Настройки пула для тестов
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }


    /**
     * JOOQ Configuration с PostgreSQL диалектом.
     */
    @Bean
    @Primary
    public Configuration jooqTestConfiguration(DataSource dataSource) {
        return new DefaultConfiguration()
                .set(dataSource)
                .set(SQLDialect.POSTGRES);
    }

    /**
     * JOOQ DSL Context.
     */
    @Bean
    @Primary
    public DSLContext dslTestContext(Configuration jooqConfiguration) {
        return DSL.using(jooqConfiguration);
    }

    @Bean
    @Primary
    public SequenceFactory jooqJdbcSequenceFactory() {
        InfiniteLongSequence tweety = new AtomicInfiniteLongSequence(1_000_000_000);

        return new SequenceFactory() {
            @Override
            public <ID> Map<String, Sequence<ID>> getSequencesForType(Class<ID> idType) {
                //noinspection unchecked
                return Map.of("tweety", (Sequence<ID>) tweety);
            }
        };
    }
}
