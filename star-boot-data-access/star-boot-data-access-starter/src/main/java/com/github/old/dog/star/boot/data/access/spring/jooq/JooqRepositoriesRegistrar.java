package com.github.old.dog.star.boot.data.access.spring.jooq;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.github.old.dog.star.boot.data.access.annotation.EnableJooqRepositories;
import com.github.old.dog.star.boot.data.access.jooq.api.JooqObjectRepository;
import com.github.old.dog.star.boot.data.access.jooq.implementation.JooqRepositoryFacade;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import com.github.old.dog.star.boot.spring.registrar.AbstractComponentRegistrar;
import com.github.old.dog.star.boot.toolbox.strings.Strings;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DAOImpl;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Регистратор JOOQ репозиториев, который автоматически сканирует classpath
 * и создает бины {@link JooqObjectRepository} для найденных DAO классов и интерфейсов.
 *
 * <p>Наследует от {@link AbstractComponentRegistrar} и специализируется на
 * работе с JOOQ DAO классами (наследниками {@code AbstractSpringDAOImpl}) и
 * пользовательскими интерфейсами репозиториев.</p>
 *
 * @author AI Assistant
 * @since 1.0
 */
@Slf4j
public class JooqRepositoriesRegistrar extends AbstractComponentRegistrar {

    protected static final String JDBC_SEQUENCE_FACTORY_BEAN_NAME = "sequenceFactoryBeanName";

    @Override
    protected Class<? extends Annotation> getEnableAnnotationClass() {
        return EnableJooqRepositories.class;
    }

    @Override
    protected List<TypeFilter> createIncludeFilters() {
        List<TypeFilter> filters = new ArrayList<>();
        // Сканируем DAO классы
        filters.add(new AssignableTypeFilter(DAOImpl.class));
        // Сканируем интерфейсы, наследующие от JooqObjectRepository
        filters.add(new AssignableTypeFilter(JooqObjectRepository.class));
        return filters;
    }

    @Override
    protected String getComponentName() {
        return "JOOQ repositories";
    }

    /**
     * Создает кастомный сканер, который поддерживает сканирование интерфейсов.
     */
    @Override
    protected ClassPathScanningCandidateComponentProvider scannerInstance(RegistrationContext context) {
        return new InterfaceAwareCandidateComponentProvider(false);
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
            Class<?> candidateClass = ClassUtils.forName(className, getClass().getClassLoader());
            boolean debugLogging = attributes.getBoolean(ENABLE_DEBUG_LOGGING_ATTRIBUTE);

            // Проверяем тип кандидата
            if (ReflectionTools.isAssignable(candidateClass, DAOImpl.class)) {
                // Обрабатываем DAO класс
                return registerJooqRepository(registry, candidateClass, debugLogging);
            }

            if (candidateClass.isInterface()
                && ReflectionTools.isAssignable(candidateClass, JooqObjectRepository.class)
                && !Objects.equals(candidateClass, JooqObjectRepository.class)) {
                // Обрабатываем пользовательский интерфейс репозитория
                return registerCustomRepositoryInterface(registry, candidateClass, debugLogging);
            }

            return false;

        } catch (Exception e) {
            log.error(
                "Ошибка при регистрации JOOQ репозитория для класса {}: {}",
                className, e.getMessage(), e
            );
            return false;
        }
    }

    /**
     * Регистрирует отдельный JOOQ репозиторий для DAO класса.
     */
    private boolean registerJooqRepository(BeanDefinitionRegistry registry,
                                           Class<?> daoClass,
                                           boolean debugLogging) {
        if (debugLogging) {
            log.debug("Обработка DAO класса: {}", daoClass.getName());
        }

        // Извлекаем generic типы из DAO класса
        JooqTypeInfo typeInfo = extractJooqTypes(daoClass);
        if (typeInfo == null) {
            log.warn("Не удалось извлечь JOOQ типы из DAO класса: {}", daoClass.getName());
            return false;
        }

        // Создаем бин definition для репозитория
        String repositoryBeanName = generateRepositoryBeanName(daoClass);

        if (registry.containsBeanDefinition(repositoryBeanName)) {
            log.warn(
                "Bean с именем {} уже зарегистрирован, пропускаем DAO класс {}",
                repositoryBeanName, daoClass.getName()
            );
            return false;
        }

        BeanDefinition repositoryBeanDef = createRepositoryBeanDefinition(daoClass, typeInfo);

        registry.registerBeanDefinition(repositoryBeanName, repositoryBeanDef);

        if (debugLogging) {
            log.debug(
                "Зарегистрирован JOOQ репозиторий '{}' для DAO: {}",
                repositoryBeanName, daoClass.getName()
            );
        }

        return true;
    }

    /**
     * Регистрирует пользовательский интерфейс репозитория, создавая Proxy над JooqRepositoryFacade.
     */
    private boolean registerCustomRepositoryInterface(BeanDefinitionRegistry registry,
                                                      Class<?> repositoryInterface,
                                                      boolean debugLogging) {
        if (debugLogging) {
            log.debug("Обработка пользовательского интерфейса репозитория: {}", repositoryInterface.getName());
        }

        // Извлекаем generic типы из интерфейса репозитория
        JooqTypeInfo typeInfo = extractJooqTypesFromInterface(repositoryInterface);

        if (typeInfo == null) {
            log.warn("Не удалось извлечь JOOQ типы из интерфейса репозитория: {}", repositoryInterface.getName());
            return false;
        }

        // Создаем имя бина для пользовательского интерфейса
        String customRepositoryBeanName = generateCustomRepositoryBeanName(repositoryInterface);

        if (registry.containsBeanDefinition(customRepositoryBeanName)) {
            log.warn(
                "Bean с именем {} уже зарегистрирован, пропускаем интерфейс {}",
                customRepositoryBeanName, repositoryInterface.getName()
            );
            return false;
        }

        // Находим соответствующий JooqRepositoryFacade бин
        String facadeBeanName = generateRepositoryBeanName(typeInfo.daoType);

        BeanDefinition proxyBeanDef = createRepositoryProxyBeanDefinition(
            repositoryInterface, typeInfo, facadeBeanName
        );

        registry.registerBeanDefinition(customRepositoryBeanName, proxyBeanDef);

        if (debugLogging) {
            log.debug(
                "Зарегистрирован пользовательский репозиторий '{}' для интерфейса: {}",
                customRepositoryBeanName, repositoryInterface.getName()
            );
        }

        return true;
    }

    /**
     * Извлекает информацию о JOOQ типах из DAO класса.
     */
    private JooqTypeInfo extractJooqTypes(Class<?> daoClass) {
        Type genericSuperclass = daoClass.getGenericSuperclass();

        if (!(genericSuperclass instanceof ParameterizedType parameterizedType)) {
            return null;
        }

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        if (actualTypeArguments.length != 3) {
            return null;
        }

        try {
            Class<?> recordType = (Class<?>) actualTypeArguments[0];  // RECORD
            Class<?> pojoType = (Class<?>) actualTypeArguments[1];    // POJO
            Class<?> idType = (Class<?>) actualTypeArguments[2];      // ID

            return new JooqTypeInfo(recordType, pojoType, idType, daoClass);

        } catch (ClassCastException e) {
            log.warn("Не удалось привести аргументы типа для DAO: {}", daoClass.getName());
            return null;
        }
    }

    /**
     * Извлекает информацию о JOOQ типах из пользовательского интерфейса репозитория.
     */
    private JooqTypeInfo extractJooqTypesFromInterface(Class<?> repositoryInterface) {
        Class<?>[] parentGenericTypes = ReflectionTools.getParentGenericTypes(repositoryInterface, JooqObjectRepository.class);

        if (parentGenericTypes.length == 4) {
            try {
                Class<?> recordType = parentGenericTypes[0];  // RECORD
                Class<?> pojoType = parentGenericTypes[1];    // POJO
                Class<?> idType = parentGenericTypes[2];      // ID
                Class<?> daoType = parentGenericTypes[3];     // REPO

                return new JooqTypeInfo(recordType, pojoType, idType, daoType);

            } catch (ClassCastException e) {
                log.warn(
                    "Не удалось привести аргументы типа для интерфейса: {}",
                    repositoryInterface.getName()
                );
            }
        }

        return null;
    }

    /**
     * Создает BeanDefinition для JOOQ репозитория с правильной типизацией.
     */
    private BeanDefinition createRepositoryBeanDefinition(Class<?> daoClass, JooqTypeInfo typeInfo) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(JooqRepositoryFacade.class);

        // Создаем правильный ResolvableType для JooqObjectRepository<RECORD, POJO, ID, REPO>
        ResolvableType repositoryType = ResolvableType.forClassWithGenerics(
            JooqObjectRepository.class,
            typeInfo.recordType,
            typeInfo.pojoType,
            typeInfo.idType,
            typeInfo.daoType
        );

        beanDefinition.setTargetType(repositoryType);

        // Настройка constructor argument - ссылка на DAO bean
        ConstructorArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, typeInfo.idType);
        argumentValues.addIndexedArgumentValue(
            1, new RuntimeBeanReference(this.<String>extractAnnotationAttribute(attributes ->
                attributes.getString(JooqRepositoriesRegistrar.JDBC_SEQUENCE_FACTORY_BEAN_NAME)))
        );
        argumentValues.addIndexedArgumentValue(2, new RuntimeBeanReference(generateDaoBeanName(daoClass)));

        // Настройки bean
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanDefinition.setLazyInit(false);

        // Добавляем описание для лучшей отладки
        String description = String.format(
            "Автоматически созданный JOOQ репозиторий для DAO %s<%s, %s, %s>",
            daoClass.getSimpleName(),
            typeInfo.recordType.getSimpleName(),
            typeInfo.pojoType.getSimpleName(),
            typeInfo.idType.getSimpleName()
        );
        beanDefinition.setDescription(description);

        return beanDefinition;
    }

    /**
     * Создает BeanDefinition для Proxy пользовательского интерфейса репозитория.
     */
    private BeanDefinition createRepositoryProxyBeanDefinition(Class<?> repositoryInterface,
                                                               JooqTypeInfo typeInfo,
                                                               String facadeBeanName) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(JooqRepositoryProxyFactory.class);
        beanDefinition.setFactoryMethodName("createProxy");

        // Создаем правильный ResolvableType для пользовательского интерфейса
        beanDefinition.setTargetType(ResolvableType.forClass(repositoryInterface));

        // Настройка constructor arguments для factory method
        ConstructorArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, repositoryInterface);  // Интерфейс для proxy
        argumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference(facadeBeanName));  // JooqRepositoryFacade

        // Настройки bean
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanDefinition.setLazyInit(false);

        // Добавляем описание для лучшей отладки
        String description = String.format(
            "Автоматически созданный Proxy для пользовательского репозитория %s<%s, %s, %s, %s>",
            repositoryInterface.getSimpleName(),
            typeInfo.recordType.getSimpleName(),
            typeInfo.pojoType.getSimpleName(),
            typeInfo.idType.getSimpleName(),
            typeInfo.daoType.getSimpleName()
        );
        beanDefinition.setDescription(description);

        return beanDefinition;
    }

    private String generateRepositoryBeanName(Class<?> jooqRepoClass) {
        String className = jooqRepoClass.getSimpleName();
        return "jooq@repository@" + Strings.changeCase(Strings.TextCase.CAMEL_CASE, Strings.TextCase.KEBAB_CASE, className);
    }

    /**
     * Генерирует имя бина для пользовательского интерфейса репозитория.
     */
    private String generateCustomRepositoryBeanName(Class<?> repositoryInterface) {
        return StringUtils.uncapitalize(repositoryInterface.getSimpleName());
    }

    /**
     * Генерирует имя бина для DAO.
     */
    private String generateDaoBeanName(Class<?> daoClass) {
        return StringUtils.uncapitalize(daoClass.getSimpleName());
    }

    /**
     * Внутренний класс для хранения информации о JOOQ типах.
     */
    private static class JooqTypeInfo {
        final Class<?> recordType;
        final Class<?> pojoType;
        final Class<?> idType;
        final Class<?> daoType;

        JooqTypeInfo(Class<?> recordType, Class<?> pojoType, Class<?> idType, Class<?> daoType) {
            this.recordType = recordType;
            this.pojoType = pojoType;
            this.idType = idType;
            this.daoType = daoType;
        }
    }

    /**
     * Кастомный сканер, который поддерживает сканирование интерфейсов.
     * Переопределяет метод isCandidateComponent для включения интерфейсов в кандидаты.
     */
    private static class InterfaceAwareCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

        public InterfaceAwareCandidateComponentProvider(boolean useDefaultFilters) {
            super(useDefaultFilters);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();

            // Для интерфейсов, наследующих от JooqObjectRepository - разрешаем
            if (metadata.isInterface()) {
                try {
                    String className = metadata.getClassName();
                    Class<?> candidateClass = ClassUtils.forName(className, getClass().getClassLoader());

                    // Проверяем, что это интерфейс наследующий от JooqObjectRepository, но не сам JooqObjectRepository
                    if (ReflectionTools.isAssignable(candidateClass, JooqObjectRepository.class)
                        && !Objects.equals(candidateClass, JooqObjectRepository.class)) {
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("Не удалось загрузить класс для проверки: {}", metadata.getClassName());
                }
            }

            // Для обычных классов используем стандартную логику
            return super.isCandidateComponent(beanDefinition);
        }
    }
}
