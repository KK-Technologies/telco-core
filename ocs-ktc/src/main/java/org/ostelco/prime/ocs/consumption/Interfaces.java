// Converted from Kotlin: Interfaces.kt
package org.ostelco.prime.ocs.consumption

import org.ostelco.ocs.api.ActivateResponse
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo

package org.ostelco.prime.ocs.consumption

import org.ostelco.ocs.api.ActivateResponse
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo

/**
 * Ocs Requests from [OcsGrpcService] are consumed by implementation [OcsService] of [OcsAsyncRequestConsumer]
 */
public interface OcsAsyncRequestConsumer {
    public void creditControlRequestEvent(
            request: CreditControlRequestInfo,
            returnCreditControlAnswer:
            (CreditControlAnswerInfo) -> Unit)
}

/**
 * Ocs Events from [OcsEventToGrpcResponseMapper] forwarded to implementation [OcsService] of [OcsAsyncResponseProducer]
 */
public interface OcsAsyncResponseProducer {
    public void activateOnNextResponse(response: ActivateResponse)
    public void sendCreditControlAnswer(streamId: String, creditControlAnswer: CreditControlAnswerInfo)
    public void returnUnusedDataBucketEvent(msisdn: String, reservedBucketBytes: Long)
}