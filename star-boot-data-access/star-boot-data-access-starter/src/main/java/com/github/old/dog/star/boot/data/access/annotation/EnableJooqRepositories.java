package com.github.old.dog.star.boot.data.access.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.github.old.dog.star.boot.data.access.jooq.api.JooqObjectRepository;
import com.github.old.dog.star.boot.data.access.spring.jooq.JooqRepositoriesRegistrar;

/**
 * Включает автоматическую регистрацию JOOQ репозиториев в Spring контексте.
 *
 * <p>Аннотация сканирует classpath и автоматически создает бины {@link JooqObjectRepository}
 * для всех найденных JOOQ DAO классов (наследников {@code AbstractSpringDAOImpl}).</p>
 *
 * <p>Также поддерживает автоматическую регистрацию пользовательских интерфейсов,
 * наследующих от {@link JooqObjectRepository}, создавая для них Proxy объекты.</p>
 *
 * <p>Пример использования:
 * <pre>{@code
 * @Configuration
 * @EnableJooqRepositories(basePackages = "com.example.dao")
 * public class JooqConfig {
 *     // ...
 * }
 * }</pre>
 *
 * <p>После этого можно инжектить как базовые репозитории:
 * <pre>{@code
 * @Autowired
 * private JooqObjectRepository<CreditObjectRecord, CreditObjectPojo, Long, CrudCreditObjectRepository> creditObjectRepo;
 * }</pre>
 *
 * <p>Так и пользовательские интерфейсы:
 * <pre>{@code
 * @Autowired
 * private CreditObjectObjectRepository creditObjectRepository;
 * }</pre>
 *
 * @author AI Assistant
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JooqRepositoriesRegistrar.class)
public @interface EnableJooqRepositories {

    /**
     * Базовые пакеты для сканирования JOOQ DAO классов и пользовательских интерфейсов репозиториев.
     * Если не указано, используется пакет аннотированного класса.
     *
     * @return массив базовых пакетов
     */
    String[] basePackages() default {};

    /**
     * Базовые классы для определения пакетов сканирования.
     * Альтернатива {@link #basePackages()}.
     *
     * @return массив базовых классов
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Исключения из сканирования - классы или пакеты, которые нужно пропустить.
     *
     * @return массив паттернов для исключения
     */
    String[] excludePatterns() default {};

    /**
     * Имя фабрики для получения последовательностей генерации ключей.
     */
    String sequenceFactoryBeanName() default "jooqJdbcSequenceFactory";

    /**
     * Включить или отключить автоматическое создание репозиториев.
     * По умолчанию включено.
     *
     * @return true если автосоздание включено
     */
    boolean enabled() default true;

    /**
     * Включает детальное логирование процесса регистрации репозиториев.
     *
     * <p>При включении выводится дополнительная информация о:</p>
     * <ul>
     *   <li>Сканируемых пакетах</li>
     *   <li>Найденных классах репозитория и пользовательских интерфейсах</li>
     *   <li>Процессе регистрации bean-ов и proxy объектов</li>
     *   <li>Используемой фабрике</li>
     * </ul>
     *
     * @return {@code true} для включения отладочного логирования
     */
    boolean enableDebugLogging() default false;

    /**
     * Включает поддержку пользовательских интерфейсов репозиториев.
     * По умолчанию включено.
     *
     * <p>При включении сканируются интерфейсы, наследующие от {@link JooqObjectRepository},
     * и для них создаются Proxy бины, делегирующие вызовы к соответствующему
     * {@link JooqRepositoryFacade}.</p>
     *
     * @return {@code true} для включения поддержки пользовательских интерфейсов
     */
    boolean enableCustomInterfaces() default true;
}
