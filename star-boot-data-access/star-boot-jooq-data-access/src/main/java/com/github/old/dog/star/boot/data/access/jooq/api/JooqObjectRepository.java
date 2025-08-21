package com.github.old.dog.star.boot.data.access.jooq.api;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jooq.DAO;
import org.jooq.DSLContext;
import org.jooq.TableRecord;
import com.github.old.dog.star.boot.data.access.jooq.api.dao.JooqDaoOperation;
import com.github.old.dog.star.boot.data.access.jooq.api.dsl.JooqDslOperation;
import com.github.old.dog.star.boot.data.access.jooq.api.template.JooqTemplate;
import com.github.old.dog.star.boot.data.access.jooq.api.template.JooqTemplateOperation;

/**
 * Базовый интерфейс для репозиториев JOOQ, предоставляющий стандартные операции
 * для работы с базой данных через JOOQ DSL и DAO.
 *
 * <p>Поддерживает различные типы операций:
 * <ul>
 *   <li>Атомарные операции с транзакциями (@Transactional)</li>
 *   <li>Простые операции без транзакций</li>
 *   <li>Шаблонные операции с дополнительной функциональностью</li>
 * </ul>
 *
 * @param <RECORD> тип записи таблицы JOOQ (extends TableRecord)
 * @param <POJO>   тип POJO объекта для маппинга
 * @param <ID>     тип первичного ключа
 * @param <REPO>   тип DAO репозитория
 * @author AI Assistant
 * @since 1.0
 */
public interface JooqObjectRepository<RECORD extends TableRecord<RECORD>, POJO, ID, REPO extends DAO<RECORD, POJO, ID>> {

    // ================================================================================================
    // Основные операции (существующие)
    // ================================================================================================

    /**
     * Generates the next unique identifier for the specified sequence key.
     * This method typically interacts with a database sequence to produce
     * sequential, unique IDs that are associated with the given sequence key.
     *
     * @param sequenceKey the key identifying the database sequence for generating IDs
     * @return the next unique identifier from the specified sequence key
     */
    ID generateNextId(@NotNull String sequenceKey);

    /**
     * Выполняет атомарную операцию с DSL контекстом в рамках транзакции.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation операция для выполнения с DSL контекстом
     * @return результат выполнения операции
     * @throws RuntimeException если операция завершилась ошибкой
     */
    <O> O atomicOperation(JooqDslOperation<O> operation);

    /**
     * Выполняет атомарную операцию с DAO репозиторием в рамках транзакции.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation операция для выполнения с DAO репозиторием
     * @return результат выполнения операции
     * @throws RuntimeException если операция завершилась ошибкой
     */
    <O> O atomicOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation);

    /**
     * Выполняет операцию с использованием расширенного шаблона репозитория.
     * Предоставляет дополнительную функциональность: генерация ID, batch операции и т.д.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation операция для выполнения с шаблоном
     * @return результат выполнения операции
     * @throws RuntimeException если операция завершилась ошибкой
     */
    <O> O templateOperation(JooqTemplateOperation<O, RECORD, POJO, ID, REPO> operation);

    /**
     * Выполняет простую операцию с DSL контекстом без управления транзакциями.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation операция для выполнения с DSL контекстом
     * @return результат выполнения операции
     * @throws RuntimeException если операция завершилась ошибкой
     */
    <O> O simpleOperation(JooqDslOperation<O> operation);

    /**
     * Выполняет простую операцию с DAO репозиторием без управления транзакциями.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation операция для выполнения с DAO репозиторием
     * @return результат выполнения операции
     * @throws RuntimeException если операция завершилась ошибкой
     */
    <O> O simpleOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation);

    // ================================================================================================
    // Convenience методы для простого использования (НОВЫЕ)
    // ================================================================================================

    /**
     * Convenience метод для простых DSL операций.
     * Позволяет использовать простые лямбды без указания типов.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation функция для выполнения с DSL контекстом
     * @return результат выполнения операции
     */
    default <O> O simpleDslOperation(Function<DSLContext, O> operation) {
        return simpleOperation(operation::apply);
    }

    /**
     * Convenience метод для простых DAO операций.
     * Позволяет использовать простые лямбды без указания типов.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation функция для выполнения с DAO репозиторием
     * @return результат выполнения операции
     */
    default <O> O simpleDaoOperation(Function<REPO, O> operation) {
        return simpleOperation(operation::apply);
    }

    /**
     * Convenience метод для атомарных DSL операций.
     * Позволяет использовать простые лямбды без указания типов.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation функция для выполнения с DSL контекстом в транзакции
     * @return результат выполнения операции
     */
    default <O> O atomicDslOperation(Function<DSLContext, O> operation) {
        return atomicOperation(operation::apply);
    }

    /**
     * Convenience метод для атомарных DAO операций.
     * Позволяет использовать простые лямбды без указания типов.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation функция для выполнения с DAO репозиторием в транзакции
     * @return результат выполнения операции
     */
    default <O> O atomicDaoOperation(Function<REPO, O> operation) {
        return atomicOperation(operation::apply);
    }

    /**
     * Convenience метод для template операций.
     * Позволяет использовать простые лямбды без указания типов.
     *
     * @param <O>       тип возвращаемого результата
     * @param operation функция для выполнения с template
     * @return результат выполнения операции
     */
    default <O> O template(Function<JooqTemplate<RECORD, POJO, ID, REPO>, O> operation) {
        return templateOperation(operation::apply);
    }
}
