package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import com.github.old.dog.star.boot.interfaces.Converter;
import lombok.RequiredArgsConstructor;

/**
 * A decorator for {@link Switchboard.Fallback} that handles type conversion.
 * <p>
 * This decorator is used when the decorated fallback operates on a different type
 * than what is required by the client code. It uses converters to transform data
 * between the external type R and the internal type T used by the decorated fallback.
 *
 * @param <T> the type used by the decorated fallback
 * @param <R> the type exposed by this decorator
 */
@RequiredArgsConstructor
public class ConverterFallbackDecorator<T, R> implements FallbackDecorator<R, T> {

    /**
     * Конвертер для преобразования байтов в объект типа T.
     */
    private final Converter<T, R> toType;

    /**
     * Конвертер для преобразования объекта типа T в байты.
     */
    private final Converter<R, T> fromType;

    /**
     * Декорируемый объект
     */
    private final Switchboard.Fallback<T> decorator;

    /**
     * Retrieves data from the decorated fallback and converts it to the external type.
     *
     * @return the converted data from the decorated fallback
     */
    @Override
    public R get() {
        return toType.convert(decorator.get());
    }

    /**
     * Accepts data of the external type, converts it to the internal type,
     * and passes it to the decorated fallback.
     *
     * @param r the data to be accepted in the external type
     * @return the condition returned by the decorated fallback
     */
    @Override
    public Switchboard.Condition accept(R r) {
        return decorator.accept(fromType.convert(r));
    }

    /**
     * Returns the decorated fallback.
     *
     * @return the fallback that this decorator wraps
     */
    @Override
    public Switchboard.Fallback<T> decorates() {
        return decorator;
    }
}
