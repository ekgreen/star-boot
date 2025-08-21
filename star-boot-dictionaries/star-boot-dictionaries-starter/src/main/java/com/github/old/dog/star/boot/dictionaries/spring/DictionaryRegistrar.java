package com.github.old.dog.star.boot.dictionaries.spring;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import com.github.old.dog.star.boot.dictionaries.annotation.EnableDictionaries;
import com.github.old.dog.star.boot.dictionaries.api.Dictionary;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryTable;
import com.github.old.dog.star.boot.spring.registrar.AbstractComponentRegistrar;
import com.github.old.dog.star.boot.toolbox.strings.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

/**
 * Регистратор bean-определений для автоматического создания справочников.
 *
 * <p>Наследует от {@link AbstractComponentRegistrar} и специализируется на
 * сканировании классов с аннотацией {@code @DictionaryTable} и создании
 * для них bean-определений типа {@code Dictionary}.</p>
 *
 * @author AI Assistant
 * @since 1.0
 */
@Slf4j
public class DictionaryRegistrar extends AbstractComponentRegistrar {

    private static final String DICTIONARY_FACTORY_BEAN_NAME_ATTRIBUTE = "dictionaryFactoryBeanName";

    @Override
    protected Class<? extends Annotation> getEnableAnnotationClass() {
        return EnableDictionaries.class;
    }

    @Override
    protected List<TypeFilter> createIncludeFilters() {
        return Collections.singletonList(new AnnotationTypeFilter(DictionaryTable.class));
    }

    @Override
    protected String getComponentName() {
        return "dictionaries";
    }

    @Override
    protected boolean processCandidateComponent(BeanDefinitionRegistry registry,
                                              BeanDefinition candidate,
                                              AnnotationAttributes attributes) {

        String className = candidate.getBeanClassName();
        if (className == null) {
            return false;
        }

        try {
            Class<?> dictionaryClass = ClassUtils.forName(className, getClass().getClassLoader());
            String factoryBeanName = attributes.getString(DICTIONARY_FACTORY_BEAN_NAME_ATTRIBUTE);
            boolean debugLogging = attributes.getBoolean(ENABLE_DEBUG_LOGGING_ATTRIBUTE);

            return registerDictionaryBean(registry, dictionaryClass, factoryBeanName, debugLogging);

        } catch (Exception e) {
            log.error("Ошибка при регистрации словаря для класса {}: {}",
                     className, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Регистрирует bean-определение для словаря.
     */
    private boolean registerDictionaryBean(BeanDefinitionRegistry registry,
                                         Class<?> dictionaryClass,
                                         String factoryBeanName,
                                         boolean debugLogging) {

        DictionaryTable annotation = dictionaryClass.getAnnotation(DictionaryTable.class);
        if (annotation == null) {
            log.warn("Класс {} не содержит аннотацию @DictionaryTable", dictionaryClass.getName());
            return false;
        }

        String tableName = determineTableName(annotation);
        String beanName = generateBeanName(dictionaryClass);

        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Bean с именем {} уже зарегистрирован, пропускаем класс {}",
                    beanName, dictionaryClass.getName());
            return false;
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(Dictionary.class);

        // Создаем ResolvableType для Dictionary<T> где T = dictionaryClass
        ResolvableType dictionaryType = ResolvableType.forClassWithGenerics(
                Dictionary.class,
                dictionaryClass
        );

        beanDefinition.setTargetType(dictionaryType);

        // Конфигурируем зависимости через указанную фабрику
        beanDefinition.setFactoryMethodName("createDictionary");
        beanDefinition.setFactoryBeanName(factoryBeanName);

        // Передаем параметры
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, dictionaryClass);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, tableName);

        // Настройки bean
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanDefinition.setLazyInit(false);

        // Добавляем описание для лучшей отладки
        String description = String.format("Автоматически созданный словарь для класса %s (таблица: %s, фабрика: %s)",
                dictionaryClass.getSimpleName(), tableName, factoryBeanName);
        beanDefinition.setDescription(description);

        registry.registerBeanDefinition(beanName, beanDefinition);

        if (debugLogging) {
            log.debug("Зарегистрирован словарь: bean='{}', класс='{}', таблица='{}', фабрика='{}'",
                     beanName, dictionaryClass.getSimpleName(), tableName, factoryBeanName);
        }

        return true;
    }

    /**
     * Определяет имя таблицы для словаря.
     */
    private String determineTableName(DictionaryTable annotation) {
        return annotation.schema() + "." + annotation.table();
    }

    /**
     * Генерирует имя bean для словаря.
     */
    private String generateBeanName(Class<?> dictionaryClass) {
        String className = dictionaryClass.getSimpleName();
        return "dictionaries@" + Strings.changeCase(Strings.TextCase.CAMEL_CASE, Strings.TextCase.KEBAB_CASE, className);
    }
}
