package com.github.old.dog.star.boot.identifier.snowflake;

import com.github.old.dog.star.boot.toolbox.collections.sequences.EpochTimestampContinuingSequence;
import com.github.old.dog.star.boot.toolbox.collections.sequences.InfiniteLongSequence;
import com.github.old.dog.star.boot.toolbox.collections.sequences.InfiniteShortSequence;
import com.github.old.dog.star.boot.toolbox.collections.sequences.SpinningShortContinuingSequence;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * Генератор уникальных идентификаторов, основанный на алгоритме Snowflake ID.
 * <p>
 * Snowflake ID - это 64-битный идентификатор, состоящий из следующих компонентов:
 * <ul>
 *   <li>41 бит - временная метка (timestamp)</li>
 *   <li>10 бит - идентификатор машины (machine ID)</li>
 *   <li>12 бит - последовательный номер (sequence)</li>
 *   <li>1 бит - зарезервирован для знака</li>
 * </ul>
 * <p>
 * Такая структура обеспечивает:
 * <ul>
 *   <li>Уникальность идентификаторов</li>
 *   <li>Сортируемость по времени создания</li>
 *   <li>Распределенную генерацию без центральной координации</li>
 *   <li>Высокую производительность (до 4096 ID в миллисекунду на одну машину)</li>
 * </ul>
 * <p>
 * Генератор реализует интерфейс {@link InfiniteLongSequence}, что позволяет использовать
 * его как источник бесконечной последовательности уникальных идентификаторов.
 *
 * @see SnowflakeID Структура Snowflake ID
 */
@Builder
@RequiredArgsConstructor
public class SnowflakeGenerator implements InfiniteLongSequence {

    /**
     * Идентификатор машины (сервера), на которой выполняется генерация.
     * <p>
     * Это значение занимает 10 бит в структуре Snowflake ID, что позволяет
     * использовать до 1024 различных машин (от 0 до 1023) без конфликтов.
     */
    private final short machineId;

    /**
     * Генератор временных меток.
     * <p>
     * Этот компонент отвечает за генерацию временной части идентификатора,
     * которая занимает 41 бит. Обычно используется количество миллисекунд
     * с заданной эпохи.
     */
    private final InfiniteLongSequence timestamp;

    /**
     * Генератор последовательных номеров.
     * <p>
     * Этот компонент обеспечивает уникальность идентификаторов, созданных
     * в рамках одной миллисекунды. Значение занимает 12 бит, что позволяет
     * генерировать до 4096 уникальных ID в миллисекунду на одной машине.
     */
    private final InfiniteShortSequence machineSequence;

    /**
     * Создает генератор Snowflake ID, совместимый с форматом Twitter.
     * <p>
     * Этот фабричный метод создает экземпляр генератора с параметрами,
     * соответствующими оригинальной реализации Twitter (сейчас X):
     * <ul>
     *   <li>41 бит для временной метки</li>
     *   <li>10 бит для идентификатора машины</li>
     *   <li>12 бит для последовательного номера</li>
     * </ul>
     * <p>
     * Для генерации временной метки используется системная эпоха,
     * а для последовательных номеров - циклический генератор с максимальным
     * значением 1024.
     *
     * @param machineId идентификатор машины (от 0 до 1023)
     * @return настроенный генератор Snowflake ID
     */
    public static SnowflakeGenerator tweetyPie(short machineId) {
        return new SnowflakeGenerator(
                machineId,
                EpochTimestampContinuingSequence.systemEpoch(41),
                new SpinningShortContinuingSequence(10, (short) 1024)
        );
    }

    /**
     * Генерирует следующий уникальный идентификатор Snowflake ID.
     * <p>
     * Метод создает новый идентификатор, используя:
     * <ul>
     *   <li>Текущую временную метку от генератора timestamp</li>
     *   <li>Заданный идентификатор машины machineId</li>
     *   <li>Следующий последовательный номер от генератора machineSequence</li>
     * </ul>
     * <p>
     * Реализация обеспечивает потокобезопасность и высокую производительность
     * благодаря использованию специализированных последовательностей.
     *
     * @return уникальный 64-битный идентификатор Snowflake
     */
    @Override
    public long next() {
        return SnowflakeID.builder()
                .timestamp(timestamp.next())
                .machineld(machineId)
                .machineSeq(machineSequence.next())
                .buildLong();
    }
}
