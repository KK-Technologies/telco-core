// Converted from Kotlin: DataSourceTypes.kt
package org.ostelco.ocsgw.datasource


package org.ostelco.ocsgw.datasource

enum public class DataSourceType {
    Local,
    gRPC,
    PubSub,
    Proxy
}

enum public class SecondaryDataSourceType {
    gRPC,
    PubSub
}