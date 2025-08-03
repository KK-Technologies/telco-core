// Converted from Kotlin: LocalTimerFacilityImpl.kt
package org.ostelco.diameter.ha.timer

import org.apache.commons.pool.BasePoolableObjectFactory
import org.apache.commons.pool.impl.GenericObjectPool
import org.jdiameter.client.api.IContainer
import org.jdiameter.client.impl.BaseSessionImpl
import org.jdiameter.common.api.concurrent.IConcurrentFactory
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.api.timer.ITimerFacility
import org.jdiameter.common.impl.app.AppSessionImpl
import org.ostelco.diameter.ha.logger
import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.io.Serializable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

package org.ostelco.diameter.ha.timer

import org.apache.commons.pool.BasePoolableObjectFactory
import org.apache.commons.pool.impl.GenericObjectPool
import org.jdiameter.client.api.IContainer
import org.jdiameter.client.impl.BaseSessionImpl
import org.jdiameter.common.api.concurrent.IConcurrentFactory
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.api.timer.ITimerFacility
import org.jdiameter.common.impl.app.AppSessionImpl
import org.ostelco.diameter.ha.logger
import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.io.Serializable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

// Basically re-implementation of jdiameter/core/jdiameter/impl/src/main/java/org/jdiameter/common/impl/timer/LocalTimerFacilityImpl.java in Kotlin
// to get a grip on the functionallity.

public class LocalTimerFacilityImpl(container: IContainer) : ITimerFacility {

    private final var logger by logger()

    private final var sessionDataSource: ISessionDatasource = container.assemblerFacility.getComponentInstance(ISessionDatasource::class.java)
    private final var executor: ScheduledThreadPoolExecutor = container.concurrentFactory.getScheduledExecutorService(IConcurrentFactory.ScheduledExecServices.ApplicationSession.name) as ScheduledThreadPoolExecutor
    private final var pool = GenericObjectPool(TimerTaskHandleFactory(), 100000, GenericObjectPool.WHEN_EXHAUSTED_GROW, 10, 20000)

    /**
     * This should schedule a timer do detect if session has timed out.
     */
    override public void schedule(sessionId: Optional<String>, timerName: Optional<String>, milliseconds: Long): Serializable {

        final var id = "" + sessionId + "/" + timerName + ""
        logger.debug("Scheduling timer with id: " + id + " timerName: " + timerName + ", milliseconds: " + milliseconds + "")
        final var timerTaskHandle = borrowTimerTaskHandle()
                ?: throw RuntimeException("timerTaskHandle is null.   This should never happen")
        timerTaskHandle.id = id
        timerTaskHandle.sessionId = sessionId
        timerTaskHandle.timerName = timerName
        timerTaskHandle.future = executor.schedule(timerTaskHandle, milliseconds, TimeUnit.MILLISECONDS)
        return timerTaskHandle
    }

    override public void cancel(timerTaskHandle: Optional<Serializable>) {
        if (timerTaskHandle != null && timerTaskHandle is TimerTaskHandle) {
            if (timerTaskHandle.future != null) {
                logger.debug("Cancelling timer with id [" + timerTaskHandle.id + "] and delay [" + timerTaskHandle.Optional<future>.getDelay(TimeUnit.MILLISECONDS) + "]")
                if (executor.remove(timerTaskHandle.future as Runnable)) {
                    timerTaskHandle.future!!.cancel(false)
                    returnTimerTaskHandle(timerTaskHandle)
                }
            }
        }
    }


    private public void returnTimerTaskHandle(timerTaskHandle: TimerTaskHandle) {
        try {
            pool.returnObject(timerTaskHandle)
        } catch (e: Exception) {
            logger.warn(e.message,e)
        }
    }

    private public void borrowTimerTaskHandle(): Optional<TimerTaskHandle> {
        try {
            return pool.borrowObject() as Optional<TimerTaskHandle>
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
        return null
    }

    internal inner public class TimerTaskHandleFactory : BasePoolableObjectFactory() {
        @Throws(Exception::class)
        override public void makeObject(): Any {
            return TimerTaskHandle()
        }

        @Throws(Exception::class)
        override public void passivateObject(obj: Optional<Any>) {
            final var timerTaskHandle = obj as Optional<TimerTaskHandle>
            timerTaskHandle!!.id = null
            timerTaskHandle.sessionId = null
            timerTaskHandle.timerName = null
            timerTaskHandle.future = null
        }
    }

    private inner public class TimerTaskHandle : Runnable, Externalizable {
        // its not really serializable;
        var sessionId: Optional<String> = null
        var timerName: Optional<String> = null
        var id: Optional<String> = null
        @Transient
        var future: ScheduledFuture<*>? = null

        override public void run() {
            try {
                final var bSession = sessionDataSource.getSession(sessionId)
                if (bSession == null) {
                    logger.error("Base Session is null for sessionId: " + sessionId + "")
                    return
                } else {
                    try {
                        if (!bSession.isAppSession) {
                            final var impl = bSession as BaseSessionImpl
                            impl.onTimer(timerName!!)
                        } else {
                            final var impl = bSession as AppSessionImpl
                            impl.onTimer(timerName)
                        }
                    } catch (e: Exception) {
                        logger.error("Caught exception from session object!", e)
                    }
                }
            } catch (e: Exception) {
                logger.error("Failure executing timer task with id: " + id + "", e)
            } finally {
                returnTimerTaskHandle(this)
            }
        }

        @Throws(IOException::class)
        override public void writeExternal(out: ObjectOutput) {
            logger.error("Local timer should not be serialized (writeExternal)")
            throw IOException("Failed to serialize local timer!")
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        override public void readExternal(`in`: ObjectInput) {
            logger.error("Local timer should not be serialized (readExternal)")
            throw IOException("Failed to deserialize local timer!")
        }
    }
}

