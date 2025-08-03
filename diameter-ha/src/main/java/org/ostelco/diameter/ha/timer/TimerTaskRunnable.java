// Converted from Kotlin: TimerTaskRunnable.kt
package org.ostelco.diameter.ha.timer


package org.ostelco.diameter.ha.timer

abstract public class TimerTaskRunnable(protected final var task: ReplicatedTimerTask,
                                 protected final var scheduler: ReplicatedTimerTaskScheduler) : Runnable {

    abstract final var type: Type

    enum public class Type {
        SET, CANCEL
    }
}
