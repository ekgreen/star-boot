package com.github.old.dog.star.boot.data.access.spring.jooq;

import com.github.old.dog.star.boot.data.access.jooq.api.JooqObjectRepository;
import com.github.old.dog.star.boot.reflection.ReflectionTools;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Фабрика для создания Proxy объектов пользовательских интерфейсов репозиториев.
 *
 * <p>Создает динамические прокси, которые перенаправляют все вызовы методов
 * к соответствующему экземпляру {@link JooqObjectRepository}.</p>
 *
 * @author AI Assistant
 * @since 1.0
 */
@Slf4j
public class JooqRepositoryProxyFactory {

    /**
     * Создает Proxy для пользовательского интерфейса репозитория.
     *
     * @param repositoryInterface класс интерфейса для создания proxy
     * @param facadeRepository    экземпляр JooqRepositoryFacade для делегирования вызовов
     * @param <T>                 тип интерфейса репозитория
     * @return Proxy объект, реализующий указанный интерфейс
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> repositoryInterface,
                                    JooqObjectRepository<?, ?, ?, ?> facadeRepository) {

        if (!repositoryInterface.isInterface()) {
            throw new IllegalArgumentException(
                "repositoryInterface должен быть интерфейсом: " + repositoryInterface.getName()
            );
        }

        if (ReflectionTools.notAssignable(repositoryInterface, JooqObjectRepository.class)) {
            throw new IllegalArgumentException(
                "repositoryInterface должен наследовать от JooqObjectRepository: "
                + repositoryInterface.getName()
            );
        }

        log.debug("Создание Proxy для интерфейса: {}", repositoryInterface.getName());

        return (T) Proxy.newProxyInstance(
            repositoryInterface.getClassLoader(),
            new Class<?>[]{repositoryInterface},
            new JooqRepositoryInvocationHandler(repositoryInterface, facadeRepository)
        );
    }

    /**
     * InvocationHandler для перенаправления вызовов методов к JooqRepositoryFacade.
     */
    private static class JooqRepositoryInvocationHandler implements InvocationHandler {

        private final Class<?> repositoryInterface;
        private final JooqObjectRepository<?, ?, ?, ?> facadeRepository;

        public JooqRepositoryInvocationHandler(Class<?> repositoryInterface,
                                               JooqObjectRepository<?, ?, ?, ?> facadeRepository) {
            this.repositoryInterface = repositoryInterface;
            this.facadeRepository = facadeRepository;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Обрабатываем методы Object класса
            if (method.getDeclaringClass() == Object.class) {
                return handleObjectMethod(proxy, method, args);
            }

            // Все остальные методы делегируем к facade
            if (log.isTraceEnabled()) {
                log.trace("Делегирование вызова метода {} к JooqRepositoryFacade", method.getName());
            }

            return method.invoke(facadeRepository, args);
        }

        /**
         * Обрабатывает методы класса Object.
         */
        private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();

            return switch (methodName) {
                case "toString" -> String.format(
                    "JooqRepositoryProxy[%s] -> %s",
                    repositoryInterface.getSimpleName(),
                    facadeRepository.toString()
                );
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> {
                    try {
                        yield method.invoke(facadeRepository, args);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка при вызове метода " + methodName, e);
                    }
                }
            };
        }
    }
}
