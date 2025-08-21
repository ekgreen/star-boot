package com.github.old.dog.star.boot.throwbles;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class providing methods to handle throwable exceptions and to check
 * if a Runnable throws an exception during execution.
 * <p>
 * This class is not intended to be instantiated, as it contains only static utility methods.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Throwables {

    /**
     * Throws a given {@link Throwable} as an unchecked exception, bypassing the checked exception constraint.
     * This method uses type erasure to allow the rethrowing of checked exceptions without the need to declare them.
     *
     * @param <R> the return type of the method
     * @param <E> the type of the exception being thrown
     * @param e   the throwable to be thrown
     * @return an instance of the return type (though it practically never returns as it throws the provided exception)
     * @throws E the type of the exception being thrown
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <R, E extends Throwable> R sneakyThrow(Throwable e) throws E {
        // noinspection unchecked
        throw (E) e;
    }

    /**
     * Checks if the given Runnable throws a Throwable when executed.
     *
     * @param runnable the Runnable to be executed and checked for any thrown exceptions
     * @return true if the Runnable throws a Throwable during execution, false otherwise
     */
    public static boolean isThrowble(Runnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Throwable exception) {
            return true;
        }
    }

}
