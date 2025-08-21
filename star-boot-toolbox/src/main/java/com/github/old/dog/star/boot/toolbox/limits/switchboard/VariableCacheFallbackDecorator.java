package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import lombok.RequiredArgsConstructor;

/**
 * A decorator for {@link Switchboard.Fallback} that adds in-memory caching.
 * <p>
 * This decorator stores the result of the decorated fallback in a simple instance
 * variable, which can improve performance by avoiding repeated data fetches from
 * the underlying fallback source. Unlike abstract LocalCacheFallbackDecorator, this
 * implementation doesn't use Cache abstraction.
 *
 * @param <T> the type of data stored in the cache
 */
@RequiredArgsConstructor
public class VariableCacheFallbackDecorator<T> implements FallbackDecorator<T, T> {

    /**
     * Декорируемый объект
     */
    private final Switchboard.Fallback<T> decorator;

    /**
     * Переменная для сохранения результата
     */
    private T value;

    /**
     * Retrieves data from the cache, or from the decorated fallback if not cached.
     * <p>
     * The first time this method is called, it retrieves data from the decorated
     * fallback and caches it. Subsequent calls return the cached value without
     * calling the decorated fallback again, until the cache is updated via
     * {@link #accept(Object)}.
     *
     * @return the cached data or data from the decorated fallback
     */
    @Override
    public T get() {

        if (this.value == null) {
            this.value = decorator.get();
        }

        return this.value;
    }

    /**
     * Accepts new data, passes it to the decorated fallback, and updates the cached value.
     * <p>
     * This method ensures that subsequent calls to {@link #get()} will return the
     * newly accepted data without calling the decorated fallback.
     *
     * @param t the new data to be accepted and cached
     * @return the condition returned by the decorated fallback
     */
    @Override
    public Switchboard.Condition accept(T t) {
        Switchboard.Condition condition = decorator.accept(t);
        this.value = t;
        return condition;
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
