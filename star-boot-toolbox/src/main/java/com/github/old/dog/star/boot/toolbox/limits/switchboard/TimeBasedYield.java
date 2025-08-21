package com.github.old.dog.star.boot.toolbox.limits.switchboard;

import java.time.LocalDateTime;

/**
 * Time-based implementation of the Switchboard condition.
 * <p>
 * This class determines whether the Switchboard should use the fallback
 * based on a scheduled next update time. It yields true until the current
 * time reaches or exceeds the next update time.
 */
public class TimeBasedYield implements Switchboard.Condition {

    /**
     * The scheduled time for the next data update.
     */
    private final LocalDateTime nextUpdateTime;

    /**
     * Creates a new time-based condition with the specified next update time.
     *
     * @param nextUpdateTime the scheduled time for the next update
     */
    public TimeBasedYield(LocalDateTime nextUpdateTime) {
        this.nextUpdateTime = nextUpdateTime;
    }

    /**
     * Checks whether the fallback should be used based on the current time.
     * <p>
     * Returns true when the current time has not yet reached or exceeded the
     * next update time, indicating that the cached data is still valid and
     * the fallback should be used.
     *
     * @return true if the fallback should be used, false if it's time for a fresh update
     */
    @Override
    public boolean yield() {
        return !(LocalDateTime.now().isAfter(nextUpdateTime) || LocalDateTime.now().isEqual(nextUpdateTime));
    }

}
