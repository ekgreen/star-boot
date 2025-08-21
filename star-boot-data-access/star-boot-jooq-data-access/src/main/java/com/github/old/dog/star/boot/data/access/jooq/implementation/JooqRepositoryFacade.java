package com.github.old.dog.star.boot.data.access.jooq.implementation;

import com.github.old.dog.star.boot.data.access.api.SequenceFactory;
import com.github.old.dog.star.boot.data.access.jooq.api.JooqObjectRepository;
import com.github.old.dog.star.boot.data.access.jooq.api.dao.JooqDaoOperation;
import com.github.old.dog.star.boot.data.access.jooq.api.dsl.JooqDslOperation;
import com.github.old.dog.star.boot.data.access.jooq.api.template.JooqTemplate;
import com.github.old.dog.star.boot.data.access.jooq.api.template.JooqTemplateOperation;
import com.github.old.dog.star.boot.data.access.jooq.api.template.JooqTransactionalOperation;
import com.github.old.dog.star.boot.interfaces.Sequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.DAO;
import org.jooq.TableRecord;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.function.Supplier;

/**
 * Базовая реализация репозитория JOOQ, предоставляющая стандартные операции
 * для работы с базой данных через JOOQ DSL и DAO.
 *
 * <p>Класс обеспечивает:
 * <ul>
 *   <li>Управление транзакциями через Spring @Transactional</li>
 *   <li>Логирование всех операций с проверкой уровня логирования</li>
 *   <li>Унифицированную обработку ошибок</li>
 *   <li>Поддержку различных типов операций (атомарные, простые, шаблонные)</li>
 *   <li>Управление последовательностями для генерации ключей</li>
 * </ul>
 *
 * @param <RECORD> тип записи таблицы JOOQ (extends TableRecord)
 * @param <POJO>   тип POJO объекта для маппинга
 * @param <ID>     тип первичного ключа
 * @param <REPO>   тип DAO репозитория
 * @author AI Assistant
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class JooqRepositoryFacade<RECORD extends TableRecord<RECORD>, POJO, ID, REPO extends DAO<RECORD, POJO, ID>>
    implements JooqObjectRepository<RECORD, POJO, ID, REPO> {

    /**
     * Класс ключа сущности
     */
    private final Class<ID> idType;

    /**
     * Thread-safe хранилище последовательностей для генерации ключей.
     */
    private final SequenceFactory sequences;

    /**
     * JOOQ DAO репозиторий для выполнения операций с базой данных.
     */
    private final REPO repository;

    // ================================================================================================
    // Public API Methods
    // ================================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public <O> O atomicOperation(JooqDslOperation<O> operation) {
        return executeWithLoggingAndErrorHandling(
            "atomic DSL operation",
            () -> operation.doThrowableOperation(repository.configuration().dsl())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public <O> O atomicOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation) {
        return executeWithLoggingAndErrorHandling(
            "atomic DAO operation",
            () -> operation.doThrowableOperation(repository)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <O> O templateOperation(JooqTemplateOperation<O, RECORD, POJO, ID, REPO> operation) {
        return executeWithLoggingAndErrorHandling(
            "template operation",
            () -> operation.doThrowableOperation(new TemplateOperations<>(this))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <O> O simpleOperation(JooqDslOperation<O> operation) {
        return executeWithLoggingAndErrorHandling(
            "simple DSL operation",
            () -> operation.doThrowableOperation(repository.configuration().dsl())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <O> O simpleOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation) {
        return executeWithLoggingAndErrorHandling(
            "simple DAO operation",
            () -> operation.doThrowableOperation(repository)
        );
    }

    // ================================================================================================
    // Utility Methods for Functional Approach
    // ================================================================================================

    /**
     * Выполняет операцию с унифицированным логированием и обработкой ошибок.
     *
     * @param operationName название операции для логирования
     * @param operation     функция для выполнения
     * @param <O>           тип возвращаемого результата
     * @return результат выполнения операции
     * @throws RuntimeException если операция завершилась ошибкой
     */
    private <O> O executeWithLoggingAndErrorHandling(String operationName, Supplier<O> operation) {
        if (log.isDebugEnabled()) {
            log.debug("Starting {} for table: {}", operationName, getTableName());
        }

        long startTime = System.currentTimeMillis();

        try {
            O result = operation.get();
            logSuccessfulOperation(operationName, startTime);
            return result;

        } catch (Exception e) {
            logFailedOperation(operationName, startTime, e);
            throw e; // Перебрасываем исключение для правильной работы транзакций
        }
    }

    /**
     * Логирует успешное выполнение операции.
     *
     * @param operationName название операции
     * @param startTime     время начала выполнения
     */
    private void logSuccessfulOperation(String operationName, long startTime) {
        if (log.isDebugEnabled()) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("{} completed successfully in {}ms for table: {}",
                operationName, executionTime, getTableName());
        }
    }

    /**
     * Логирует неудачное выполнение операции.
     *
     * @param operationName название операции
     * @param startTime     время начала выполнения
     * @param exception     возникшее исключение
     */
    private void logFailedOperation(String operationName, long startTime, Exception exception) {
        long executionTime = System.currentTimeMillis() - startTime;
        log.error("{} failed after {}ms for table {}: {}",
            operationName, executionTime, getTableName(), exception.getMessage(), exception);
    }

    /**
     * Валидирует параметры для настройки последовательности.
     *
     * @param sequenceKey ключ последовательности
     * @param sequence    объект последовательности
     * @throws IllegalArgumentException если параметры некорректны
     */
    private void validateSequenceParameters(String sequenceKey, Sequence<?> sequence) {
        if (sequenceKey == null || sequenceKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Sequence key cannot be null or empty");
        }
        if (sequence == null) {
            throw new IllegalArgumentException("Sequence cannot be null");
        }
    }

    /**
     * Получает имя таблицы для логирования.
     *
     * @return имя таблицы или "unknown" если не удалось определить
     */
    private String getTableName() {
        try {
            return repository.getTable().getName();
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("Could not determine table name: {}", e.getMessage());
            }
            return "unknown";
        }
    }

    /**
     * Получает последовательность по ключу.
     *
     * @return последовательность или null если не найдена
     */
    public ID generateNextId(@NotNull String sequenceKey) {
        return this.executeWithLoggingAndErrorHandling(
            "generate next ID",
            () -> {
                Sequence<ID> sequence = sequences.getSequenceForTypeAndKey(idType, sequenceKey);

                if (sequence == null) {
                    throw new IllegalStateException("Sequence not configured");
                }

                ID nextId = (ID) sequence.nextValue();

                if (log.isDebugEnabled()) {
                    log.debug("Generated ID: {} for sequence: {}", nextId, "default sequence");
                }

                return nextId;
            }
        );
    }

    // ================================================================================================
    // Inner RepositoryTemplate Class
    // ================================================================================================

    /**
     * Внутренний класс, предоставляющий расширенную функциональность репозитория.
     * Реализует интерфейс JooqTemplate с дополнительными возможностями.
     *
     * @param <RECORD> тип записи таблицы JOOQ
     * @param <POJO>   тип POJO объекта
     * @param <ID>     тип первичного ключа
     * @param <REPO>   тип DAO репозитория
     */
    @RequiredArgsConstructor
    private static class TemplateOperations<RECORD extends TableRecord<RECORD>, POJO, ID, REPO extends DAO<RECORD, POJO, ID>>
        implements JooqTemplate<RECORD, POJO, ID, REPO> {

        private final JooqRepositoryFacade<RECORD, POJO, ID, REPO> parentRepository;

        /**
         * {@inheritDoc}
         */
        @Override
        public ID generateNextId(@NotNull String sequenceKey) {
            return parentRepository.generateNextId(sequenceKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void transactional(JooqTransactionalOperation operation) {
            // Делегируем транзакционную операцию родительскому объекту
            parentRepository.atomicDslOperation(dsl -> {
                operation.doThrowableOperation(null);
                return null;
            });

        }

        @Override
        public <O> O atomicOperation(JooqDslOperation<O> operation) {
            return parentRepository.atomicOperation(operation);
        }

        @Override
        public <O> O atomicOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation) {
            return parentRepository.atomicOperation(operation);
        }

        @Override
        public <O> O simpleOperation(JooqDslOperation<O> operation) {
            return parentRepository.simpleOperation(operation);
        }

        @Override
        public <O> O simpleOperation(JooqDaoOperation<O, RECORD, POJO, ID, REPO> operation) {
            return parentRepository.simpleOperation(operation);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void batchInsert(List<POJO> forInsert) {
            validateBatchList(forInsert, "batch insert");

            parentRepository.executeWithLoggingAndErrorHandling(
                String.format("batch insert (%d records)", forInsert.size()),
                () -> {
                    parentRepository.repository.insert(forInsert);
                    return null;
                }
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void batchUpdate(List<POJO> forUpdate) {
            validateBatchList(forUpdate, "batch update");

            parentRepository.executeWithLoggingAndErrorHandling(
                String.format("batch update (%d records)", forUpdate.size()),
                () -> {
                    parentRepository.repository.update(forUpdate);
                    return null;
                }
            );
        }

        /**
         * Валидирует список для batch операций.
         *
         * @param list          список для валидации
         * @param operationName название операции для логирования
         */
        private void validateBatchList(List<POJO> list, String operationName) {
            if (list == null || list.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping {} - empty list", operationName);
                }
                return;
            }
        }
    }
}
