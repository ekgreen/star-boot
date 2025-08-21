package com.github.old.dog.star.boot.toolbox.limits.switchboard;

/**
 * Interface for the Decorator pattern applied to {@link Switchboard.Fallback}.
 * <p>
 * This interface defines a decorator that wraps another fallback instance, potentially
 * adding behavior or transforming data. It provides access to the decorated fallback
 * through the {@link #decorates()} method.
 *
 * @param <T> the type exposed by this decorator
 * @param <R> the type used by the decorated fallback
 */
public interface FallbackDecorator<T, R> extends Switchboard.Fallback<T> {
    /**
     * Returns the fallback that this decorator wraps.
     *
     * @return the decorated fallback
     */
    Switchboard.Fallback<R> decorates();

    /**
     * Recursively unwraps nested decorators to find the core fallback implementation,
     * and tries to get an initial condition from it if it supports initialization.
     * <p>
     * This method is used to initialize the Switchboard with the appropriate condition
     * based on the state of the innermost fallback.
     *
     * @param fallback the possibly decorated fallback
     * @param defaultCondition the default condition to use if the core fallback doesn't support initialization
     * @return the initial condition from the core fallback or the default condition
     */
    public static Switchboard.Condition tryInitialCondition(Switchboard.Fallback<?> fallback, Switchboard.Condition defaultCondition) {
        Switchboard.Fallback<?> recursive = fallback;

        while (recursive instanceof FallbackDecorator<?, ?> decorator) {
            recursive = decorator.decorates();
        }

        return recursive instanceof Switchboard.FallbackWithInit<?> withInit ? withInit.init() : defaultCondition;
    }

}
