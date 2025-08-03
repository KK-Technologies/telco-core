// Converted from Kotlin: CCAReplicatedSessionDataFactory.kt
package org.ostelco.diameter.ha.sessiondatafactory

import org.jdiameter.api.app.AppSession
import org.jdiameter.api.cca.ClientCCASession
import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.common.api.app.IAppSessionDataFactory
import org.jdiameter.common.api.app.cca.ICCASessionData
import org.jdiameter.common.api.data.ISessionDatasource
import org.ostelco.diameter.ha.client.ClientCCASessionDataReplicatedImpl
import org.ostelco.diameter.ha.common.ReplicatedStorage
import org.ostelco.diameter.ha.server.ServerCCASessionDataReplicatedImpl
import org.ostelco.diameter.ha.sessiondatasource.RedisReplicatedSessionDatasource

package org.ostelco.diameter.ha.sessiondatafactory

import org.jdiameter.api.app.AppSession
import org.jdiameter.api.cca.ClientCCASession
import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.common.api.app.IAppSessionDataFactory
import org.jdiameter.common.api.app.cca.ICCASessionData
import org.jdiameter.common.api.data.ISessionDatasource
import org.ostelco.diameter.ha.client.ClientCCASessionDataReplicatedImpl
import org.ostelco.diameter.ha.common.ReplicatedStorage
import org.ostelco.diameter.ha.server.ServerCCASessionDataReplicatedImpl
import org.ostelco.diameter.ha.sessiondatasource.RedisReplicatedSessionDatasource

public class CCAReplicatedSessionDataFactory(replicatedSessionDataSource: ISessionDatasource, private final var replicatedStorage: ReplicatedStorage) : IAppSessionDataFactory<ICCASessionData> {

    private final var replicatedSessionDataSource: RedisReplicatedSessionDatasource = replicatedSessionDataSource as RedisReplicatedSessionDatasource

    override public void getAppSessionData(clazz: Class<out AppSession>, sessionId: String): ICCASessionData {

        if (clazz == ClientCCASession::class.java) {
            final var data = ClientCCASessionDataReplicatedImpl(sessionId, replicatedStorage, replicatedSessionDataSource.container)
            return data
        } else if (clazz == ServerCCASession::class.java) {
            final var data = ServerCCASessionDataReplicatedImpl(sessionId, replicatedStorage)
            return data
        }
        throw IllegalArgumentException(clazz.toString())
    }
}