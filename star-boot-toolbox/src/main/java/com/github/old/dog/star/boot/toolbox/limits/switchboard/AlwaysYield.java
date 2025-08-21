package com.github.old.dog.star.boot.toolbox.limits.switchboard;

/**
 * Implementation of {@link Switchboard.Condition} that always yields true.
 * <p>
 * This is a simple condition that can be used when a fallback mechanism should
 * always be used, regardless of other factors.
 */
public class AlwaysYield implements Switchboard.Condition {

    /**
     * Always returns true, indicating that the fallback mechanism should be used.
     *
     * @return always true
     */
    @Override
    public boolean yield() {
        return true;
    }

}
