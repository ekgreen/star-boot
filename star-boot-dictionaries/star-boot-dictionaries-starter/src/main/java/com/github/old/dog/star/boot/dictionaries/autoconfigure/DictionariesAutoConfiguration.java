package com.github.old.dog.star.boot.dictionaries.autoconfigure;

import com.github.old.dog.star.boot.dictionaries.api.Dictionaries;
import com.github.old.dog.star.boot.dictionaries.api.Dictionary;
import com.github.old.dog.star.boot.dictionaries.implementation.DictionaryFacade;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryFactory;
import com.github.old.dog.star.boot.dictionaries.engines.jdbc.JdbcDictionaryFactory;
import java.util.List;

@AutoConfiguration
public class DictionariesAutoConfiguration {

    @ConditionalOnClass(
        value = {JdbcTemplate.class, JdbcDictionaryFactory.class}
    )
    @Configuration(proxyBeanMethods = false)
    public static class JdbcDictionaryConfiguration {

        @Bean
        @ConditionalOnMissingBean(JdbcDictionaryFactory.class)
        public DictionaryFactory jdbcDictionaryFactory(JdbcTemplate jdbcTemplate) {
            return new JdbcDictionaryFactory(jdbcTemplate);
        }

        @Bean
        @ConditionalOnMissingBean(Dictionaries.class)
        public Dictionaries dictionaries(List<Dictionary<?>> dictionaries) {
            return new DictionaryFacade(dictionaries);
        }
    }


}
