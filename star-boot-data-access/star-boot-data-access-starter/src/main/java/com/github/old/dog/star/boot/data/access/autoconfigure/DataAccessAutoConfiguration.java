package com.github.old.dog.star.boot.data.access.autoconfigure;

import java.util.List;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import com.github.old.dog.star.boot.data.access.api.SequenceFactory;
import com.github.old.dog.star.boot.data.access.implementation.JdbcSequenceRegistrar;
import com.github.old.dog.star.boot.interfaces.Sequence;

@AutoConfiguration
public class DataAccessAutoConfiguration {

    @ConditionalOnClass(value = DSLContext.class)
    @Configuration(proxyBeanMethods = false)
    public static class JooqAccessAutoConfiguration {

        @Bean("jooqJdbcSequenceFactory")
        @ConditionalOnMissingBean(SequenceFactory.class)
        public SequenceFactory jooqJdbcSequenceFactory(List<Sequence<?>> sequences) {
            return new JdbcSequenceRegistrar(sequences);
        }

        @Bean
        @ConditionalOnMissingBean(DSLContext.class)
        public DSLContext dslContext(org.jooq.Configuration globalConfiguration) {
            return DSL.using(globalConfiguration);
        }

        @Bean
        @ConditionalOnMissingBean(org.jooq.Configuration.class)
        public org.jooq.Configuration globalConfiguration(DataSource dataSource) {
            // Кастомные настройки JOOQ
            Settings settings = new Settings()
                .withExecuteWithOptimisticLockingExcludeUnversioned(true)
                .withQueryTimeout(10)
                // Форматирование SQL для логов
                .withRenderFormatted(true)
                // Логирование выполняемых запросов
                .withExecuteLogging(true)
                // Предупреждения о производительности
                .withExecuteWithOptimisticLocking(true)
                // Размер batch операций
                .withBatchSize(1000);

            TransactionAwareDataSourceProxy transactionAwareDataSource =
                new TransactionAwareDataSourceProxy(dataSource);

            // Полная конфигурация
            return new DefaultConfiguration()
                .set(SQLDialect.POSTGRES)
                .set(transactionAwareDataSource)
                .set(settings);
        }

    }
}
