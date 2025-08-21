package com.github.old.dog.star.boot.toolbox.collections.sequences;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import com.github.old.dog.star.boot.interfaces.ShortConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Utility class providing implementations of {@link Spliterator} interfaces for
 * working with sequences.
 * <p>
 * This class contains specialized spliterator implementations for different primitive
 * types (int, long, short) and for infinite sequences, allowing integration with the
 * Java Stream API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Spliterators {


    /**
     * A specialized {@link Spliterator} for short values.
     * <p>
     * This interface extends {@link OfPrimitive} to provide a spliterator
     * that works with primitive short values, allowing for more efficient processing
     * without boxing/unboxing overhead.
     */
    @SuppressWarnings("overloads")
    public interface OfShort extends Spliterator.OfPrimitive<Short, ShortConsumer, OfShort> {

        /**
         * Performs the given action on the next element, returning false if no
         * elements remain.
         *
         * @param action the action to perform on the next element
         * @return always true for infinite spliterators
         */
        @Override
        boolean tryAdvance(ShortConsumer action);

        /**
         * {@inheritDoc}
         * <p>
         * This default implementation always returns null, as infinite spliterators
         * cannot be effectively split.
         *
         * @return always null
         */
        @Override
        default OfShort trySplit() {
            return null;
        }

        /**
         * {@inheritDoc}
         * <p>
         * This default implementation returns {@link Long#MAX_VALUE} to indicate
         * that the spliterator contains an effectively infinite number of elements.
         *
         * @return always {@link Long#MAX_VALUE}
         */
        @Override
        default long estimateSize() {
            return Long.MAX_VALUE;
        }

        /**
         * {@inheritDoc}
         * <p>
         * This default implementation repeatedly calls {@link #tryAdvance} until
         * it returns false (which should not happen for infinite spliterators).
         *
         * @param action the action to perform on each element
         */
        @Override
        default void forEachRemaining(ShortConsumer action) {
            do {
            } while (tryAdvance(action));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation adapts between {@link Consumer} and {@link ShortConsumer}.
         * If the action is already a {@link ShortConsumer}, it is used directly;
         * otherwise, it is adapted to accept primitive short values.
         *
         * @param action the action to perform on the next element
         * @return always true for infinite spliterators
         */
        @Override
        default boolean tryAdvance(Consumer<? super Short> action) {
            if (action instanceof ShortConsumer) {
                return tryAdvance((ShortConsumer) action);
            } else {
                return tryAdvance((ShortConsumer) action::accept);
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation adapts between {@link Consumer} and {@link ShortConsumer}.
         * If the action is already a {@link ShortConsumer}, it is used directly;
         * otherwise, it is adapted to accept primitive short values.
         *
         * @param action the action to perform on each element
         */
        @Override
        default void forEachRemaining(Consumer<? super Short> action) {
            if (action instanceof ShortConsumer) {
                forEachRemaining((ShortConsumer) action);
            } else {
                forEachRemaining((ShortConsumer) action::accept);
            }
        }
    }

    /**
     * Container class for consumer implementations that hold the last accepted value.
     * <p>
     * This class provides specialized consumers for different primitive types that
     * can be used to capture and store values during spliterator traversal.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Holders {

        /**
         * A consumer implementation for int values that stores the last accepted value.
         * <p>
         * This class can be used to capture int values during spliterator traversal.
         */
        public static final class OfInt implements IntConsumer {
            /**
             * The last int value accepted by this consumer.
             */
            @Getter
            private int value;

            /**
             * Accepts and stores an int value.
             *
             * @param value the int value to accept
             */
            @Override
            public void accept(int value) {
                this.value = value;
            }
        }

        /**
         * A consumer implementation for short values that stores the last accepted value.
         * <p>
         * This class can be used to capture short values during spliterator traversal.
         */
        public static final class OfShort implements ShortConsumer {
            /**
             * The last short value accepted by this consumer.
             */
            @Getter
            private short value;

            /**
             * Accepts and stores a short value.
             *
             * @param value the short value to accept
             */
            @Override
            public void accept(short value) {
                this.value = value;
            }
        }

    }

    /**
     * Container class for infinite spliterator implementations.
     * <p>
     * This class provides specialized spliterator implementations for different
     * primitive types that wrap {@link InfiniteIntSequence}, {@link InfiniteLongSequence},
     * and {@link InfiniteShortSequence} instances, allowing them to be used with
     * the Java Stream API.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Infinite {

        /**
         * An implementation of {@link Spliterators.OfShort} that wraps an {@link InfiniteShortSequence}.
         * <p>
         * This class allows an infinite sequence of short values to be used with the Java Stream API.
         * It returns characteristics indicating that the spliterator is ordered, non-null, and concurrent.
         */
        @RequiredArgsConstructor
        public static class OfShort implements Spliterators.OfShort, InfiniteShortSequence {

            public final InfiniteShortSequence sequence;

            /**
             * {@inheritDoc}
             * <p>
             * This implementation returns characteristics indicating that the spliterator
             * is ordered, non-null, and concurrent.
             *
             * @return the characteristics of this spliterator
             */
            @Override
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.CONCURRENT;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation gets the next short value from the sequence and passes
             * it to the action, always returning true to indicate that more elements
             * are available.
             *
             * @param action the action to perform on the next element
             * @return always true, as this is an infinite spliterator
             */
            @Override
            public boolean tryAdvance(ShortConsumer action) {
                short value = this.next();
                action.accept(value);
                return true;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation delegates to the wrapped sequence.
             *
             * @return the next short value in the sequence
             */
            @Override
            public short next() {
                return sequence.next();
            }
        }

        /**
         * An implementation of {@link OfInt} that wraps an {@link InfiniteIntSequence}.
         * <p>
         * This class allows an infinite sequence of int values to be used with the Java Stream API.
         * It returns characteristics indicating that the spliterator is ordered, non-null, and concurrent.
         */
        @RequiredArgsConstructor
        public static class OfInt implements Spliterator.OfInt, InfiniteIntSequence {

            public final InfiniteIntSequence sequence;

            /**
             * {@inheritDoc}
             * <p>
             * This implementation returns characteristics indicating that the spliterator
             * is ordered, non-null, and concurrent.
             *
             * @return the characteristics of this spliterator
             */
            @Override
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.CONCURRENT;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation gets the next int value from the sequence and passes
             * it to the action, always returning true to indicate that more elements
             * are available.
             *
             * @param action the action to perform on the next element
             * @return always true, as this is an infinite spliterator
             */
            @Override
            public boolean tryAdvance(IntConsumer action) {
                int value = this.next();
                action.accept(value);
                return true;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation delegates to the wrapped sequence.
             *
             * @return the next int value in the sequence
             */
            @Override
            public int next() {
                return sequence.next();
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation always returns null, as infinite spliterators
             * cannot be effectively split.
             *
             * @return always null
             */
            @Override
            public OfInt trySplit() {
                return null;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation returns {@link Long#MAX_VALUE} to indicate
             * that the spliterator contains an effectively infinite number of elements.
             *
             * @return always {@link Long#MAX_VALUE}
             */
            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

        }

        /**
         * An implementation of {@link OfLong} that wraps an {@link InfiniteLongSequence}.
         * <p>
         * This class allows an infinite sequence of long values to be used with the Java Stream API.
         * It returns characteristics indicating that the spliterator is ordered, non-null, and concurrent.
         */
        @RequiredArgsConstructor
        public static class OfLong implements Spliterator.OfLong, InfiniteLongSequence {

            public final InfiniteLongSequence sequence;

            /**
             * {@inheritDoc}
             * <p>
             * This implementation returns characteristics indicating that the spliterator
             * is ordered, non-null, and concurrent.
             *
             * @return the characteristics of this spliterator
             */
            @Override
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.CONCURRENT;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation gets the next long value from the sequence and passes
             * it to the action, always returning true to indicate that more elements
             * are available.
             *
             * @param action the action to perform on the next element
             * @return always true, as this is an infinite spliterator
             */
            @Override
            public boolean tryAdvance(LongConsumer action) {
                long value = this.next();
                action.accept(value);
                return true;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation delegates to the wrapped sequence.
             *
             * @return the next long value in the sequence
             */
            @Override
            public long next() {
                return sequence.next();
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation always returns null, as infinite spliterators
             * cannot be effectively split.
             *
             * @return always null
             */
            @Override
            public OfLong trySplit() {
                return null;
            }

            /**
             * {@inheritDoc}
             * <p>
             * This implementation returns {@link Long#MAX_VALUE} to indicate
             * that the spliterator contains an effectively infinite number of elements.
             *
             * @return always {@link Long#MAX_VALUE}
             */
            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

        }
    }
}
