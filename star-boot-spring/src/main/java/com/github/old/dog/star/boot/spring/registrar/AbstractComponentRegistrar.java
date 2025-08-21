package com.github.old.dog.star.boot.spring.registrar;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Абстрактный базовый класс для автоматической регистрации Spring компонентов
 * на основе сканирования classpath и аннотаций.
 *
 * <p>Предоставляет общую функциональность для:
 * <ul>
 *   <li>Извлечения конфигурации из enable-аннотаций</li>
 *   <li>Сканирования classpath с настраиваемыми фильтрами</li>
 *   <li>Регистрации bean definitions в Spring контексте</li>
 *   <li>Унифицированного логирования процесса регистрации</li>
 * </ul>
 *
 * <p>Подклассы должны реализовать абстрактные методы для специфичной логики:
 * <ul>
 *   <li>{@link #getEnableAnnotationClass()} - класс enable-аннотации</li>
 *   <li>{@link #createIncludeFilters()} - фильтры для сканирования</li>
 *   <li>{@link #processCandidateComponent(BeanDefinitionRegistry, BeanDefinition, AnnotationAttributes)} - обработка найденного компонента</li>
 * </ul>
 *
 * @author AI Assistant
 * @since 1.0
 */
@Slf4j
public abstract class AbstractComponentRegistrar implements ImportBeanDefinitionRegistrar {

    // Стандартные имена атрибутов в enable-аннотациях
    protected static final String BASE_PACKAGES_ATTRIBUTE = "basePackages";
    protected static final String BASE_PACKAGE_CLASSES_ATTRIBUTE = "basePackageClasses";
    protected static final String EXCLUDE_PATTERNS_ATTRIBUTE = "excludePatterns";
    protected static final String ENABLED_ATTRIBUTE = "enabled";
    protected static final String ENABLE_DEBUG_LOGGING_ATTRIBUTE = "enableDebugLogging";

    private final AtomicReference<AnnotationAttributes> annotationAttributesAccess
        = new AtomicReference<>();

    @Override
    public final void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                              @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = extractAnnotationAttributes(importingClassMetadata);

        if (attributes == null || !isRegistrationEnabled(attributes)) {
            logRegistrationDisabled();
            return;
        }

        RegistrationContext context = createRegistrationContext(importingClassMetadata, attributes);

        logRegistrationStart(context);

        int registeredCount = performRegistration(registry, context);

        logRegistrationComplete(context, registeredCount);
    }

    /**
     * Возвращает класс enable-аннотации для данного регистратора.
     *
     * @return класс аннотации (например, EnableDictionaries.class)
     */
    protected abstract Class<? extends Annotation> getEnableAnnotationClass();

    /**
     * Создает фильтры для включения компонентов в сканирование.
     *
     * @return список фильтров для сканирования
     */
    protected abstract List<TypeFilter> createIncludeFilters();

    /**
     * Обрабатывает найденный компонент-кандидат и регистрирует соответствующий bean.
     *
     * @param registry   реестр bean definitions
     * @param candidate  найденный компонент-кандидат
     * @param attributes атрибуты enable-аннотации
     * @return true если компонент был успешно обработан
     */
    protected abstract boolean processCandidateComponent(BeanDefinitionRegistry registry,
                                                         BeanDefinition candidate,
                                                         AnnotationAttributes attributes);

    /**
     * Возвращает имя компонента для логирования (например, "dictionaries", "jooq repositories").
     *
     * @return имя компонента
     */
    protected abstract String getComponentName();

    // ================================================================================================
    // Template Method Implementation
    // ================================================================================================

    /**
     * Извлекает атрибуты enable-аннотации.
     */
    private AnnotationAttributes extractAnnotationAttributes(AnnotationMetadata metadata) {
        String annotationName = getEnableAnnotationClass().getName();
        Map<String, Object> attributeMap = metadata.getAnnotationAttributes(annotationName);

        if (attributeMap == null) {
            log.warn("Аннотация {} не найдена, регистрация {} пропущена",
                getEnableAnnotationClass().getSimpleName(), getComponentName());
            return null;
        }

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributeMap);
        this.annotationAttributesAccess.set(annotationAttributes);

        return annotationAttributes;
    }

    /**
     * Extracts a specific attribute from the annotation attributes using the provided access function.
     *
     * @param <T>    the type of the attribute value to be extracted
     * @param access a function that defines how to extract the desired attribute value
     *               from the annotation attributes
     * @return the extracted annotation attribute value
     */
    protected <T> T extractAnnotationAttribute(Function<AnnotationAttributes, T> access) {
        return access.apply(this.annotationAttributesAccess.get());
    }

    /**
     * Проверяет, включена ли регистрация.
     */
    private boolean isRegistrationEnabled(AnnotationAttributes attributes) {
        return attributes.getBoolean(ENABLED_ATTRIBUTE);
    }

    /**
     * Создает контекст регистрации с настройками.
     */
    private RegistrationContext createRegistrationContext(AnnotationMetadata metadata,
                                                          AnnotationAttributes attributes) {

        Set<String> basePackages = determineBasePackages(metadata, attributes);
        String[] excludePatterns = attributes.getStringArray(EXCLUDE_PATTERNS_ATTRIBUTE);
        boolean debugLogging = attributes.getBoolean(ENABLE_DEBUG_LOGGING_ATTRIBUTE);

        return new RegistrationContext(basePackages, excludePatterns, debugLogging, attributes);
    }

    /**
     * Определяет базовые пакеты для сканирования.
     */
    private Set<String> determineBasePackages(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Set<String> basePackages = new LinkedHashSet<>();

        // Из атрибута basePackages
        Arrays.stream(attributes.getStringArray(BASE_PACKAGES_ATTRIBUTE))
            .filter(StringUtils::hasText)
            .forEach(basePackages::add);

        // Из атрибута basePackageClasses
        Arrays.stream(attributes.getClassArray(BASE_PACKAGE_CLASSES_ATTRIBUTE))
            .map(ClassUtils::getPackageName)
            .forEach(basePackages::add);

        // Если ничего не указано - используем пакет аннотированного класса
        if (basePackages.isEmpty()) {
            String packageName = ClassUtils.getPackageName(metadata.getClassName());
            basePackages.add(packageName);
        }

        return basePackages;
    }

    /**
     * Выполняет основную логику регистрации.
     */
    private int performRegistration(BeanDefinitionRegistry registry, RegistrationContext context) {
        ClassPathScanningCandidateComponentProvider scanner = createScanner(context);

        int totalRegistered = 0;

        for (String basePackage : context.getBasePackages()) {
            if (context.isDebugLogging()) {
                log.debug("Сканирование пакета {} для поиска {}", basePackage, getComponentName());
            }

            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);

            for (BeanDefinition candidate : candidates) {
                try {
                    if (processCandidateComponent(registry, candidate, context.getAttributes())) {
                        totalRegistered++;
                    }
                } catch (Exception e) {
                    log.error("Ошибка при обработке компонента {}: {}",
                        candidate.getBeanClassName(), e.getMessage(), e);
                }
            }
        }

        return totalRegistered;
    }


    /**
     * Создает сканер classpath с настроенными фильтрами.
     * Подклассы могут переопределить этот метод для использования кастомного сканера.
     */
    protected ClassPathScanningCandidateComponentProvider createScanner(RegistrationContext context) {
        ClassPathScanningCandidateComponentProvider scanner = this.scannerInstance(context);

        // Добавляем include фильтры
        createIncludeFilters().forEach(scanner::addIncludeFilter);

        // Добавляем exclude фильтры на основе excludePatterns
        createExcludeFilters(context.getExcludePatterns()).forEach(scanner::addExcludeFilter);

        return scanner;
    }

    /**
     * Creates an instance of a classpath scanner configured to scan for
     * candidate components without default filters.
     *
     * @param context the registration context containing settings such as
     *                base packages, exclude patterns, and debug logging preferences
     * @return an instance of ClassPathScanningCandidateComponentProvider
     * configured without default filters
     */
    protected ClassPathScanningCandidateComponentProvider scannerInstance(RegistrationContext context) {
        return new ClassPathScanningCandidateComponentProvider(false);
    }


    /**
     * Создает exclude фильтры на основе паттернов исключения.
     * Подклассы могут переопределить для кастомной логики.
     */
    protected List<TypeFilter> createExcludeFilters(String[] excludePatterns) {
        // Базовая реализация - пустой список
        // Подклассы могут добавить свою логику
        return Collections.emptyList();
    }

    // ================================================================================================
    // Logging Methods
    // ================================================================================================

    private void logRegistrationDisabled() {
        if (log.isDebugEnabled()) {
            log.debug("Регистрация {} отключена", getComponentName());
        }
    }

    private void logRegistrationStart(RegistrationContext context) {
        if (context.isDebugLogging()) {
            log.info("Начало регистрации {}. Пакеты для сканирования: {}",
                getComponentName(), context.getBasePackages());
        }
    }

    private void logRegistrationComplete(RegistrationContext context, int registeredCount) {
        log.info("Регистрация {} завершена. Зарегистрировано: {}",
            getComponentName(), registeredCount);
    }

    // ================================================================================================
    // Inner Context Class
    // ================================================================================================

    /**
     * Контекст регистрации, содержащий все настройки процесса.
     */
    protected static class RegistrationContext {
        private final Set<String> basePackages;
        private final String[] excludePatterns;
        private final boolean debugLogging;
        private final AnnotationAttributes attributes;

        public RegistrationContext(Set<String> basePackages,
                                   String[] excludePatterns,
                                   boolean debugLogging,
                                   AnnotationAttributes attributes) {
            this.basePackages = basePackages;
            this.excludePatterns = excludePatterns;
            this.debugLogging = debugLogging;
            this.attributes = attributes;
        }

        public Set<String> getBasePackages() {
            return basePackages;
        }

        public String[] getExcludePatterns() {
            return excludePatterns;
        }

        public boolean isDebugLogging() {
            return debugLogging;
        }

        public AnnotationAttributes getAttributes() {
            return attributes;
        }
    }
}
