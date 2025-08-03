// Converted from Kotlin: CancelTimerTaskRunnable.kt
package org.ostelco.diameter.ha.timer

import org.ostelco.diameter.ha.logger

package org.ostelco.diameter.ha.timer

import org.ostelco.diameter.ha.logger

public class CancelTimerTaskRunnable internal constructor(task: ReplicatedTimerTask,
                                                   scheduler: ReplicatedTimerTaskScheduler) : TimerTaskRunnable(task, scheduler) {

    private final var logger by logger()

    override final var type: Type
        get() = TimerTaskRunnable.Type.CANCEL


    override public void run() {

        logger.debug("Cancelling timer task for timer ID " + task.data.taskID + "")

        scheduler.getLocalRunningTasksMap().remove(task.data.taskID)

        try {
            task.cancel()
        } catch (e: Throwable) {
            logger.error("Failed to cancel task " + task.data.taskID + "", e)
        }
    }
}
