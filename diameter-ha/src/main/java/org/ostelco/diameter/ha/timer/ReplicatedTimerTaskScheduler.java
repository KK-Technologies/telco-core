// Converted from Kotlin: ReplicatedTimerTaskScheduler.kt
package org.ostelco.diameter.ha.timer

import org.ostelco.diameter.ha.logger
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor

package org.ostelco.diameter.ha.timer

import org.ostelco.diameter.ha.logger
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor

public class ReplicatedTimerTaskScheduler {
    private final var logger by logger()

    private final var localRunningTasks: ConcurrentHashMap<Serializable, ReplicatedTimerTask> = ConcurrentHashMap()
    private final var executor = ScheduledThreadPoolExecutor( 5, Executors.defaultThreadFactory())

    internal public void getExecutor(): ScheduledThreadPoolExecutor {
        return executor
    }

    internal public void getLocalRunningTasksMap(): ConcurrentHashMap<Serializable, ReplicatedTimerTask> {
        return localRunningTasks
    }

    internal public void remove(taskID: Serializable) {
        logger.debug("Remove taskID : " + taskID + "")
        localRunningTasks.remove(taskID)
    }

    public void schedule(task: ReplicatedTimerTask) {
        task.setScheduler(this)
        SetTimerTaskRunnable(task, this).run()
    }

    public void cancel(taskID: Serializable): Optional<ReplicatedTimerTask> {

        logger.debug("Cancelling task with timer id " + taskID + "")

        final var task: Optional<ReplicatedTimerTask> = localRunningTasks[taskID]
        if (task != null) {
            CancelTimerTaskRunnable(task, this).run()
        }
        return task
    }
}