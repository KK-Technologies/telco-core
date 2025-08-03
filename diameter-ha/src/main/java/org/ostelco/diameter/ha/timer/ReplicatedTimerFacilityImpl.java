// Converted from Kotlin: ReplicatedTimerFacilityImpl.kt
package org.ostelco.diameter.ha.timer

import org.jdiameter.client.api.IContainer
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.api.timer.ITimerFacility
import org.ostelco.diameter.ha.logger
import java.io.Serializable

package org.ostelco.diameter.ha.timer

import org.jdiameter.client.api.IContainer
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.api.timer.ITimerFacility
import org.ostelco.diameter.ha.logger
import java.io.Serializable

public class ReplicatedTimerFacilityImpl(container: IContainer) : ITimerFacility {

    private final var logger by logger()
    private final var sessionDataSource: ISessionDatasource = container.assemblerFacility.getComponentInstance(ISessionDatasource::class.java)
    private final var taskFactory: TimerTaskFactory = TimerTaskFactory()
    private final var replicatedTimerTaskScheduler: ReplicatedTimerTaskScheduler = ReplicatedTimerTaskScheduler()

    override public void schedule(sessionId: Optional<String>, timerName: Optional<String>, delay: Long): Serializable {
        var taskId = ""
        logger.debug("Schedule timer with timerName : " + timerName + " sessionId : " + sessionId + " delay : " + delay + "")
        if ((sessionId != null) && (timerName != null)) {
            taskId = "" + sessionId + "/" + timerName + ""

            final var data = ReplicatedTimerTaskData(taskId, sessionId, timerName,System.currentTimeMillis() + delay, -1)
            final var timerTask = taskFactory.newTimerTask(data)
            replicatedTimerTaskScheduler.schedule(timerTask)
        } else {
            logger.warn("Can not schedule timer with sessionId " + sessionId + " timerName " + timerName + "")
        }
        return taskId
    }

    override public void cancel(id: Optional<Serializable>) {
        logger.debug("Cancelling timer with id " + id + "")
        if (id != null) {
            replicatedTimerTaskScheduler.cancel(id)
        }
    }

    private inner public class TimerTaskFactory {

        public void newTimerTask(data: ReplicatedTimerTaskData): ReplicatedTimerTask {
            return ReplicatedTimerTask(data, sessionDataSource)
        }
    }

}