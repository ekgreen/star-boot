package com.github.old.dog.star.boot.identifier.snowflake;

import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

/**
 * Snowflake ID (description from <a href ="https://en.wikipedia.org/wiki/Snowflake_ID">Snowflake ID</a>)
 * <p>
 * Snowflake IDs, or snowflakes, are a form of unique identifier used in distributed computing. The format was created
 * by Twitter (now X) and is used for the IDs of tweets. It is popularly believed that every snowflake has a unique
 * structure, so they took the name "snowflake ID". The format has been adopted by other companies, including Discord
 * and Instagram (Instagram prohibited on the territory of the Russian Federation). The Mastodon social network uses
 * a modified version.
 * <p>
 * Snowflakes are 64 bits in binary. (Only 63 are used to fit in a signed integer.) The first 41 bits are a timestamp,
 * representing milliseconds since the chosen epoch. The next 10 bits represent a machine ID, preventing clashes.
 * Twelve more bits represent a per-machine sequence number, to allow creation of multiple snowflakes in the same
 * millisecond. The final number is generally serialized in decimal.
 * <p>
 * Snowflakes are sortable by time, because they are based on the time they were created. Additionally, the time
 * a snowflake was created can be calculated from the snowflake. This can be used to get snowflakes (and their associated
 * objects) that were created before or after a particular date.
 * <p>
 * Fixed header format:
 * High bits: [attr-tttt-tttt-tttt-tttt-tttt-tttt-tttt]
 * Low bits: [attr-tttt-tttm-mmm-mmmm-ssss-ssss-ssss]
 * <p>
 * Bit allocation:
 * t (Timestamp): 31 bits (high) + 10 bits (low)
 * m (Machine ID): 10 bits (low)
 * s (Machine Sequence): 12 bits (low)
 */
@RequiredArgsConstructor
public final class SnowflakeID implements Comparable<SnowflakeID>, Serializable {

    private final long word;

    public static Builder builder() {
        return new Builder();
    }

    public int getIntTimestamp() {
        return (int) SnowflakeMask.TIMESTAMP.get(word);
    }

    public short getMachineID() {
        return (short) SnowflakeMask.MACHINE_ID.get(word);
    }

    public short getMachineSequence() {
        return (short) SnowflakeMask.MACHINE_SEQ.get(word);
    }

    @Override
    public int compareTo(@NotNull SnowflakeID other) {
        return Long.compare(this.word, other.word);
    }

    @Data
    @Accessors(chain = true, fluent = true)
    public static class Builder {
        private long timestamp;
        private short machineld;
        private short machineSeq;

        public SnowflakeID buildId() {
            return new SnowflakeID(buildLong());
        }

        public long buildLong() {
            long word = 0x0L;

            word = SnowflakeMask.TIMESTAMP.moveAndSet(word, timestamp);
            word = SnowflakeMask.MACHINE_ID.moveAndSet(word, machineld);
            word = SnowflakeMask.MACHINE_SEQ.moveAndSet(word, machineSeq);

            return word;
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum SnowflakeMask {
        // CHECKSTYLE:OFF
        // @formatter:off
        TIMESTAMP(0x1FFFFFFFFFFL, 41, 22),
        MACHINE_ID(0x3FFL, 10, 12),
        MACHINE_SEQ(0xFFFL, 12, 0),
        ;
        // @formatter:on
        // CHECKSTYLE:ON

        // общая длина слова идентификатора
        private static final int WORD_LENGTH = 64;

        private final long mask;
        private final int length;
        private final int shift;

        public long moveAndSet(long word, long value) {
            return (word << length) | value;
        }

        public long get(long word) {
            return (word >> shift) & mask;
        }
    }
}
