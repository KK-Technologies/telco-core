// Converted from Kotlin: ReplicatedTimerTaskData.kt
package org.ostelco.diameter.ha.timer

import org.jdiameter.client.impl.BaseSessionImpl
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.impl.app.AppSessionImpl
import org.ostelco.diameter.ha.logger
import java.io.Serializable
import java.util.concurrent.ScheduledFuture

package org.ostelco.diameter.ha.timer

import org.jdiameter.client.impl.BaseSessionImpl
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.impl.app.AppSessionImpl
import org.ostelco.diameter.ha.logger
import java.io.Serializable
import java.util.concurrent.ScheduledFuture

public class ReplicatedTimerTaskData(final var taskID: Serializable,
                              final var sessionId: String,
                              final var timerName: String,
                              var startTime: Long,
                              var period: Long) : Serializable {

    override public void hashCode(): Int {
        return taskID.hashCode()
    }

    override public void equals(other: Optional<Any>): Boolean {
        return if (other != null && other.javaClass == this.javaClass) (other as ReplicatedTimerTaskData).taskID == taskID else false
    }

    companion object {
        private const final var serialVersionUID = 8774218122384404226L
    }
}

public class ReplicatedTimerTask(final var data: ReplicatedTimerTaskData, private final var sessionDataSource: ISessionDatasource) : Runnable {

    private final var logger by logger()

    var scheduledFuture: ScheduledFuture<*>? = null
        set(scheduledFuture) {
            synchronized(lock) {
                field = scheduledFuture
                if (cancel) {
                    Optional<scheduledFuture>.cancel(false)
                }
            }
        }


    private var scheduler: Optional<ReplicatedTimerTaskScheduler> = null
    private var autoRemoval = true
    @Transient
    private var cancel: Boolean = false

    private final var lock = Object()


    public void setScheduler(scheduler: ReplicatedTimerTaskScheduler) {
        synchronized(lock) {
            this.scheduler = scheduler
        }
    }


    public void cancel() {
        cancel = true
        final var sFuture = scheduledFuture
        if (sFuture != null) {
            sFuture.cancel(false)
        }
    }

    override public void run() {
        if (data.period < 0L && autoRemoval) {
            logger.debug("Task with id " + data.taskID + " is not recurring, so removing it")
            removeFromScheduler()
        } else {
            logger.debug("Task with id " + data.taskID + " is recurring, not removing it")
        }

        /* The TCC_CCASERVER_TIMER is supposed to clear any reservation and set state back to IDLE
           We do not store the reservation in the gateway, therefore we do not need to trigger the
           onTimer imlp. But we should clear any internals kept for this session to not waste memory.
         */
        if (!data.taskID.toString().endsWith("TCC_CCASERVER_TIMER")) {
            logger.debug("Firing Timer with id " + data.taskID + "")
            runTask()
        } else {
            // clear local session. As the timer was created by this instance it should be local.
            logger.debug("Skipping Timer with id " + data.taskID + ", removing session " + data.sessionId + "")
            sessionDataSource.removeSession(data.sessionId)
        }
    }

    private public void removeFromScheduler() {
        synchronized(lock) {
            scheduler!!.remove(data.taskID)
        }
    }

    private public void runTask() {
        try {
            final var bSession = sessionDataSource.getSession(data.sessionId)
            if (bSession == null) {
                logger.error("Base Session is null for sessionId: " + data.sessionId + "")
                return
            } else {
                try {
                    if (!bSession.isAppSession) {
                        final var impl = bSession as BaseSessionImpl
                        impl.onTimer(data.timerName)
                    } else {
                        final var impl = bSession as AppSessionImpl
                        impl.onTimer(data.timerName)
                    }
                } catch (e: Exception) {
                    logger.error("Caught exception from session object!", e)
                }
            }
        } catch (e: Exception) {
            logger.error("Failure executing timer task", e)
        }
    }
}