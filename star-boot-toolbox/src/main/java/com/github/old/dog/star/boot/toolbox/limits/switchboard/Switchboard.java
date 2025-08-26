package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thread-safe switchboard implementation for managing primary and fallback data sources.
 * <p>
 * The Switchboard acts as a circuit breaker pattern implementation that can switch between
 * a primary data source and a fallback mechanism based on conditions and internal state.
 * It ensures thread safety using a ReentrantLock for atomic reads/writes of the flag and condition.
 * <p>
 * Key features:
 * <ul>
 *   <li>Thread-safe switching between primary and fallback data sources</li>
 *   <li>Support for custom conditions to determine when to use fallback</li>
 *   <li>Built-in state management (RED = use fallback, GREEN = use primary)</li>
 *   <li>Support for cascading fallbacks through decorators</li>
 * </ul>
 *
 * @param <T> the type of data managed by this switchboard
 */
public class Switchboard<T> implements Function<Switchboard.Fallback<T>, T>, Supplier<T> {

    /**
     * Constant representing the RED state (use fallback).
     */
    public static final int RED = 0;

    /**
     * Constant representing the GREEN state (use primary source).
     */
    public static final int GREEN = 1;

    /**
     * Lock for ensuring thread-safe access to the flag and condition.
     */
    private final ReentrantLock flagLock = new ReentrantLock();

    /**
     * The primary data source function.
     */
    private final Supplier<T> function;

    /**
     * The fallback mechanism to use when the primary source fails or conditions are met.
     */
    private final Fallback<T> fallback;

    /**
     * The condition that determines when to switch to the fallback.
     */
    private Condition switchFlagCondition;

    /**
     * The current state of the switchboard (RED or GREEN).
     */
    private int flag;

    /**
     * Creates a new Switchboard with the specified initial state, primary function, and fallback.
     *
     * @param initialFlag the initial state of the switchboard (RED or GREEN)
     * @param function    the primary data source function
     * @param fallback    the fallback mechanism to use when needed
     * @throws IllegalArgumentException if initialFlag is not RED or GREEN
     */
    public Switchboard(
        @NotNull Integer initialFlag,
        @NotNull Supplier<T> function,
        @Nullable Switchboard.Fallback<T> fallback
    ) {
        if (initialFlag != RED && initialFlag != GREEN) {
            throw new IllegalArgumentException("Switchboard.initialFlag can not be " + initialFlag);
        }

        this.function = function;
        this.fallback = fallback;
        this.switchFlagCondition = FallbackDecorator.tryInitialCondition(fallback, () -> false);
        this.flag = initialFlag;
    }

    /**
     * Applies the provided fallback to retrieve data.
     * <p>
     * This method implements the Function interface and delegates to selectorWithFallback.
     *
     * @param fallback the fallback to use
     * @return the data from either the primary source or the fallback
     */
    @Override
    public T apply(Fallback<T> fallback) {
        return this.selectorWithFallback(fallback);
    }

    /**
     * Gets data from either the primary source or the default fallback.
     * <p>
     * This method implements the Supplier interface and delegates to selector.
     *
     * @return the data from either the primary source or the fallback
     */
    @Override
    public T get() {
        return this.selector();
    }

    /**
     * Selects between the primary source and the default fallback based on conditions.
     *
     * @return the data from either the primary source or the fallback
     * @throws IllegalStateException if no fallback is set
     */
    public T selector() {
        if (this.fallback == null) {
            throw new IllegalStateException("Switchboard.fallback must be set");
        }

        return this.selectorWithFallback(fallback);
    }

    /**
     * Core method that selects between the primary source and a specified fallback.
     * <p>
     * This method implements the circuit breaker pattern by selecting the data source
     * based on the current state and conditions. It follows these rules:
     * <ul>
     *   <li>If the state is GREEN and the condition yields true, use the fallback</li>
     *   <li>Otherwise, try to acquire the lock and get data from the primary source</li>
     *   <li>If the lock can't be acquired, use the fallback</li>
     *   <li>If the primary source succeeds, update the condition and set state to GREEN</li>
     * </ul>
     *
     * @param fallback the fallback to use if needed
     * @return the data from either the primary source or the fallback
     */
    public T selectorWithFallback(@NotNull Fallback<T> fallback) {
        Condition condition;
        int proceedFlag;

        flagLock.lock();

        try {
            proceedFlag = this.flag; // управление на уровне Switchboard
            condition = this.switchFlagCondition; // управление на уровне пользователя
        } finally {
            flagLock.unlock();
        }

        if (proceedFlag == GREEN && condition.yield()) {
            return this.tryFallback(fallback);
        }

        if (flagLock.tryLock()) {
            try {
                T t = function.get();

                this.switchFlagCondition = fallback.accept(t);
                this.flag = GREEN;

                return t;
            } finally {
                flagLock.unlock();
            }
        }

        return this.tryFallback(fallback);
    }

    /**
     * Attempts to get data from the fallback, setting the state to RED if it fails.
     * <p>
     * This method wraps the fallback.get() call in a try-catch block to handle
     * exceptions and update the state accordingly.
     *
     * @param fallback the fallback to get data from
     * @return the data from the fallback
     * @throws Exception if the fallback fails, the original exception is rethrown
     */
    public T tryFallback(@NotNull Fallback<T> fallback) {
        try {
            return fallback.get();
        } catch (Exception exception) {

            flagLock.lock();
            try {
                this.flag = RED;
            } finally {
                flagLock.unlock();
            }

            throw exception;
        }
    }

    /**
     * Interface for fallback mechanisms that can provide alternative data when the primary source fails.
     * <p>
     * A fallback both provides data through {@link #get()} and accepts new data through
     * {@link #accept(Object)}, which returns a condition for when to use the fallback.
     *
     * @param <T> the type of data handled by this fallback
     */
    public interface Fallback<T> extends Supplier<T> {
        /**
         * Retrieves data from this fallback source.
         *
         * @return the fallback data
         */
        T get();

        /**
         * Accepts new data and returns a condition that determines when to use the fallback.
         *
         * @param t the new data to store in this fallback
         * @return a condition that determines when to use this fallback
         */
        Condition accept(T t);
    }

    /**
     * Extended fallback interface that supports initialization with a condition.
     * <p>
     * This interface is used by fallbacks that need to provide an initial condition
     * based on their state at startup.
     *
     * @param <T> the type of data handled by this fallback
     */
    public interface FallbackWithInit<T> extends Fallback<T> {
        /**
         * Initializes the fallback and returns a condition based on its initial state.
         *
         * @return the initial condition for using this fallback
         */
        Condition init();
    }

    /**
     * Interface for conditions that determine when to use a fallback mechanism.
     * <p>
     * A condition is essentially a predicate that evaluates whether the fallback
     * should be used instead of the primary data source.
     */
    public interface Condition {
        /**
         * Determines whether to use the fallback mechanism.
         *
         * @return true if the fallback should be used, false otherwise
         */
        boolean yield();
    }

}
