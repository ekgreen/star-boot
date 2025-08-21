package com.github.old.dog.star.boot.dictionaries.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import com.github.old.dog.star.boot.dictionaries.api.DictionaryFactory;
import com.github.old.dog.star.boot.dictionaries.api.annotations.DictionaryTable;
import com.github.old.dog.star.boot.dictionaries.implementation.DictionaryBridge;
import com.github.old.dog.star.boot.dictionaries.spring.DictionaryRegistrar;

/**
 * Включает поддержку автоматической регистрации справочников в Spring контексте.
 *
 * <p>Аннотация сканирует указанные пакеты на наличие классов, помеченных
 * аннотацией {@code @DictionaryTable}, и автоматически регистрирует для них
 * bean-определения с типом {@code RawDictionary}.</p>
 *
 * <h3>Пример использования:</h3>
 * <pre>{@code
 * @Configuration
 * @EnableDictionaries(basePackages = "com.example.dictionaries")
 * public class DictionaryConfig {
 *     // дополнительная конфигурация
 * }
 * }</pre>
 *
 * <h3>Кастомная фабрика:</h3>
 * <pre>{@code
 * @Configuration
 * @EnableDictionaries(
 *     basePackages = "com.example.dictionaries",
 *     dictionaryFactoryBeanName = "customDictionaryFactory"
 * )
 * public class DictionaryConfig {
 *
 *     @Bean("customDictionaryFactory")
 *     public DictionaryFactory customDictionaryFactory(JdbcTemplate jdbcTemplate) {
 *         return new CustomDictionaryFactory(jdbcTemplate);
 *     }
 * }
 * }</pre>
 *
 * <h3>Автоматическая регистрация:</h3>
 * <p>Для каждого найденного класса словаря создается bean с именем в формате
 * {@code <className>Dictionary} (например, {@code userEntityDictionary}).</p>
 *
 * @author AI Assistant
 * @see DictionaryTable
 * @see DictionaryBridge
 * @see DictionaryFactory
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DictionaryRegistrar.class)
public @interface EnableDictionaries {

    /**
     * Базовые пакеты для сканирования словарных классов.
     *
     * <p>Альтернатива для {@link #basePackages()}. Позволяет указать пакеты
     * более кратко в простых случаях.</p>
     *
     * @return массив имен пакетов для сканирования
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Базовые пакеты для сканирования словарных классов.
     *
     * <p>Поддерживает паттерны Spring (например, "com.example.**").
     * Если не указано, сканирование выполняется в пакете класса с аннотацией.</p>
     *
     * @return массив имен пакетов для сканирования
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Базовые классы для определения пакетов сканирования.
     *
     * <p>Альтернатива {@link #basePackages()} с типобезопасностью.
     * Пакеты указанных классов будут использованы для сканирования.</p>
     *
     * @return массив классов, пакеты которых нужно сканировать
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Исключения из сканирования - классы или пакеты, которые нужно пропустить.
     *
     * @return массив паттернов для исключения
     */
    String[] excludePatterns() default {};

    /**
     * Имя bean фабрики для создания экземпляров справочников.
     *
     * <p>Фабрика должна иметь метод {@code createDictionary(Class<?>, String)}
     * для создания экземпляров {@code RawDictionary}. Это позволяет
     * использовать кастомную логику создания справочников.</p>
     *
     * <h4>Требования к фабрике:</h4>
     * <ul>
     *   <li>Публичный метод {@code createDictionary(Class<T> dictionaryClass, String tableName)}</li>
     *   <li>Возвращает экземпляр {@code RawDictionary<T>}</li>
     *   <li>Зарегистрирована как Spring bean</li>
     * </ul>
     *
     * @return имя bean фабрики справочников
     * @see DictionaryFactory#createDictionary(Class, String)
     */
    String dictionaryFactoryBeanName() default "dictionaryFactory";

    /**
     * Включить или отключить автоматическое создание репозиториев.
     * По умолчанию включено.
     *
     * @return true если автосоздание включено
     */
    boolean enabled() default true;

    /**
     * Включает детальное логирование процесса регистрации справочников.
     *
     * <p>При включении выводится дополнительная информация о:</p>
     * <ul>
     *   <li>Сканируемых пакетах</li>
     *   <li>Найденных классах справочников</li>
     *   <li>Процессе регистрации bean-ов</li>
     *   <li>Используемой фабрике</li>
     * </ul>
     *
     * @return {@code true} для включения отладочного логирования
     */
    boolean enableDebugLogging() default false;
}
