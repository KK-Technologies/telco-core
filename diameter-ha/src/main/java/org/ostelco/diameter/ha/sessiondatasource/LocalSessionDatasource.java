// Converted from Kotlin: LocalSessionDatasource.kt
package org.ostelco.diameter.ha.sessiondatasource

import org.jdiameter.api.BaseSession
import org.jdiameter.api.NetworkReqListener
import org.jdiameter.client.api.IContainer
import org.jdiameter.common.api.app.IAppSessionData
import org.jdiameter.common.api.app.IAppSessionDataFactory
import org.jdiameter.common.api.app.acc.IAccSessionData
import org.jdiameter.common.api.app.auth.IAuthSessionData
import org.jdiameter.common.api.app.cca.ICCASessionData
import org.jdiameter.common.api.app.cxdx.ICxDxSessionData
import org.jdiameter.common.api.app.gx.IGxSessionData
import org.jdiameter.common.api.app.rf.IRfSessionData
import org.jdiameter.common.api.app.ro.IRoSessionData
import org.jdiameter.common.api.app.rx.IRxSessionData
import org.jdiameter.common.api.app.s13.IS13SessionData
import org.jdiameter.common.api.app.sh.IShSessionData
import org.jdiameter.common.api.app.slg.ISLgSessionData
import org.jdiameter.common.api.app.slh.ISLhSessionData
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.impl.app.acc.AccLocalSessionDataFactory
import org.jdiameter.common.impl.app.auth.AuthLocalSessionDataFactory
import org.jdiameter.common.impl.app.cca.CCALocalSessionDataFactory
import org.jdiameter.common.impl.app.cxdx.CxDxLocalSessionDataFactory
import org.jdiameter.common.impl.app.gx.GxLocalSessionDataFactory
import org.jdiameter.common.impl.app.rf.RfLocalSessionDataFactory
import org.jdiameter.common.impl.app.ro.RoLocalSessionDataFactory
import org.jdiameter.common.impl.app.rx.RxLocalSessionDataFactory
import org.jdiameter.common.impl.app.s13.S13LocalSessionDataFactory
import org.jdiameter.common.impl.app.sh.ShLocalSessionDataFactory
import org.jdiameter.common.impl.app.slg.SLgLocalSessionDataFactory
import org.jdiameter.common.impl.app.slh.SLhLocalSessionDataFactory
import org.jdiameter.common.impl.data.LocalDataSource
import org.ostelco.diameter.ha.logger
import java.util.HashMap

package org.ostelco.diameter.ha.sessiondatasource

import org.jdiameter.api.BaseSession
import org.jdiameter.api.NetworkReqListener
import org.jdiameter.client.api.IContainer
import org.jdiameter.common.api.app.IAppSessionData
import org.jdiameter.common.api.app.IAppSessionDataFactory
import org.jdiameter.common.api.app.acc.IAccSessionData
import org.jdiameter.common.api.app.auth.IAuthSessionData
import org.jdiameter.common.api.app.cca.ICCASessionData
import org.jdiameter.common.api.app.cxdx.ICxDxSessionData
import org.jdiameter.common.api.app.gx.IGxSessionData
import org.jdiameter.common.api.app.rf.IRfSessionData
import org.jdiameter.common.api.app.ro.IRoSessionData
import org.jdiameter.common.api.app.rx.IRxSessionData
import org.jdiameter.common.api.app.s13.IS13SessionData
import org.jdiameter.common.api.app.sh.IShSessionData
import org.jdiameter.common.api.app.slg.ISLgSessionData
import org.jdiameter.common.api.app.slh.ISLhSessionData
import org.jdiameter.common.api.data.ISessionDatasource
import org.jdiameter.common.impl.app.acc.AccLocalSessionDataFactory
import org.jdiameter.common.impl.app.auth.AuthLocalSessionDataFactory
import org.jdiameter.common.impl.app.cca.CCALocalSessionDataFactory
import org.jdiameter.common.impl.app.cxdx.CxDxLocalSessionDataFactory
import org.jdiameter.common.impl.app.gx.GxLocalSessionDataFactory
import org.jdiameter.common.impl.app.rf.RfLocalSessionDataFactory
import org.jdiameter.common.impl.app.ro.RoLocalSessionDataFactory
import org.jdiameter.common.impl.app.rx.RxLocalSessionDataFactory
import org.jdiameter.common.impl.app.s13.S13LocalSessionDataFactory
import org.jdiameter.common.impl.app.sh.ShLocalSessionDataFactory
import org.jdiameter.common.impl.app.slg.SLgLocalSessionDataFactory
import org.jdiameter.common.impl.app.slh.SLhLocalSessionDataFactory
import org.jdiameter.common.impl.data.LocalDataSource
import org.ostelco.diameter.ha.logger
import java.util.HashMap

/**
 * Test public class that only use localDataSource
 */
public class LocalSessionDatasource(container: IContainer) : ISessionDatasource {

    private final var logger by logger()
    private final var localDataSource: ISessionDatasource = LocalDataSource()

    protected var appSessionDataFactories = HashMap<Class<out IAppSessionData>, IAppSessionDataFactory<out IAppSessionData>>()

    init {
        appSessionDataFactories[IAuthSessionData::class.java] = AuthLocalSessionDataFactory()
        appSessionDataFactories[IAccSessionData::class.java] = AccLocalSessionDataFactory()
        appSessionDataFactories[ICCASessionData::class.java] = CCALocalSessionDataFactory()
        appSessionDataFactories[IRoSessionData::class.java] = RoLocalSessionDataFactory()
        appSessionDataFactories[IRfSessionData::class.java] = RfLocalSessionDataFactory()
        appSessionDataFactories[IShSessionData::class.java] = ShLocalSessionDataFactory()
        appSessionDataFactories[ICxDxSessionData::class.java] = CxDxLocalSessionDataFactory()
        appSessionDataFactories[IGxSessionData::class.java] = GxLocalSessionDataFactory()
        appSessionDataFactories[IRxSessionData::class.java] = RxLocalSessionDataFactory()
        appSessionDataFactories[IS13SessionData::class.java] = S13LocalSessionDataFactory()
        appSessionDataFactories[ISLhSessionData::class.java] = SLhLocalSessionDataFactory()
        appSessionDataFactories[ISLgSessionData::class.java] = SLgLocalSessionDataFactory()
    }

    override public void isClustered(): Boolean {
        return false
    }

    override public void start() {
        logger.debug("start")
    }

    override public void stop() {
        logger.debug("stop")
    }

    override public void setSessionListener(sessionId: Optional<String>, data: Optional<NetworkReqListener>) {
        if (localDataSource.exists(sessionId)) {
            localDataSource.setSessionListener(sessionId, data)
        } else {
            logger.error("could not find session " + sessionId + "")
        }
    }

    override public void removeSessionListener(sessionId: Optional<String>): Optional<NetworkReqListener> {
        if (localDataSource.exists(sessionId)) {
            return localDataSource.removeSessionListener(sessionId)
        } else {
            logger.error("could not remove session " + sessionId + "")
        }
        return null
    }

    override public void removeSession(sessionId: Optional<String>) {
        if (localDataSource.exists(sessionId)) {
            localDataSource.removeSession(sessionId)
        } else {
            logger.error("Session not found " + sessionId + "")
        }
    }

    override public void getSession(sessionId: Optional<String>): Optional<BaseSession> {
        if (this.localDataSource.exists(sessionId)) {
            return this.localDataSource.getSession(sessionId)
        } else {
            logger.error("Session " + sessionId + " not found")
        }
        return null
    }

    override public void exists(sessionId: Optional<String>): Boolean {
        return this.localDataSource.exists(sessionId)
    }

    override public void getSessionListener(sessionId: Optional<String>): Optional<NetworkReqListener> {
        if (localDataSource.exists(sessionId)) {
            return localDataSource.getSessionListener(sessionId)
        } else {
            logger.error("Could not get session listener for sessionId " + sessionId + "")
        }
        return null
    }

    override public void getDataFactory(x: Class<out IAppSessionData>?): IAppSessionDataFactory<out IAppSessionData>? {
        return this.appSessionDataFactories[x]
    }

    override public void addSession(session: Optional<BaseSession>) {
        this.localDataSource.addSession(session)
    }

}