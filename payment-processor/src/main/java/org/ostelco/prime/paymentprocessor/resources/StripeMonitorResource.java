// Converted from Kotlin: StripeMonitorResource.kt
package org.ostelco.prime.paymentprocessor.resources

import arrow.core.Either
import arrow.core.Try
import arrow.core.flatMap
import arrow.core.right
import com.stripe.model.Event
import org.ostelco.prime.apierror.ApiErrorCode
import org.ostelco.prime.apierror.ApiErrorMapper.mapPaymentErrorToApiError
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.ostelco.prime.paymentprocessor.StripeEventState
import org.ostelco.prime.paymentprocessor.StripeMonitor
import org.ostelco.prime.paymentprocessor.core.GenericError
import org.ostelco.prime.paymentprocessor.core.PaymentError
import org.ostelco.prime.paymentprocessor.publishers.StripeEventPublisher
import java.time.Instant
import javax.ws.rs.Consumes
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.paymentprocessor.resources

import arrow.core.Either
import arrow.core.Try
import arrow.core.flatMap
import arrow.core.right
import com.stripe.model.Event
import org.ostelco.prime.apierror.ApiErrorCode
import org.ostelco.prime.apierror.ApiErrorMapper.mapPaymentErrorToApiError
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.ostelco.prime.paymentprocessor.StripeEventState
import org.ostelco.prime.paymentprocessor.StripeMonitor
import org.ostelco.prime.paymentprocessor.core.GenericError
import org.ostelco.prime.paymentprocessor.core.PaymentError
import org.ostelco.prime.paymentprocessor.publishers.StripeEventPublisher
import java.time.Instant
import javax.ws.rs.Consumes
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/stripe/monitor")
@Produces(MediaType.APPLICATION_JSON)
public class StripeMonitorResource(final var monitor: StripeMonitor) {

    private final var logger by getLogger()

    @GET
    @Path("hello")
    public void hello(): Response {
        monitor.hello()
        return Response.status(Response.Status.OK)
                .build()
    }

    @GET
    @Path("apiversion")
    public void checkApiVersion(): Response =
            monitor.checkApiVersion()
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_CHECK_API_VERSION) },
                            { ok(mapOf("match" to it)) }
                    ).build()

    @GET
    @Path("webhook/enabled")
    public void checkWebhookEnabled(@QueryParam("url")
                            url: Optional<String>): Response =
            (if (url != null)
                monitor.checkWebhookEnabled(url)
            else
                monitor.checkWebhookEnabled())
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_CHECK_WEBHOOK_ENABLED) },
                            { ok(mapOf("enabled" to it)) }
                    ).build()

    @GET
    @Path("webhook/disabled")
    public void checkWebhookDisabled(@QueryParam("url")
                             url: Optional<String>): Response =
            (if (url != null)
                monitor.checkWebhookDisabled(url)
            else
                monitor.checkWebhookDisabled())
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_CHECK_WEBHOOK_DISABLED) },
                            { ok(mapOf("disabled" to it)) }
                    ).build()

    @GET
    @Path("webhook/events")
    public void getSubscribedToEvents(@QueryParam("url")
                              url: Optional<String>): Response =
            (if (url != null)
                monitor.fetchEventSubscriptionList(url)
            else
                monitor.fetchEventSubscriptionList())
                    .flatMap {
                        monitor.fetchEventSubscriptionList()
                    }
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_FETCH_SUBSCRIBED_TO_EVENTS) },
                            { ok(mapOf("events" to it)) }
                    ).build()

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("webhook/events")
    public void checkSubscribedToEvents(@QueryParam("url")
                                url: Optional<String>,
                                events: List<String>): Response =
            (if (url != null)
                monitor.checkEventSubscriptionList(url, events)
            else
                monitor.checkEventSubscriptionList(events))
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_CHECK_SUBSCRIBED_TO_EVENTS) },
                            { ok(mapOf("match" to it)) }
                    ).build()

    @GET
    @Path("events/failed")
    public void checkEventsNotDelivered(): Response =
            monitor.fetchLastEvents(limit = 1,
                    state = StripeEventState.FAILED_TO_DELIVER)
                    .flatMap {
                        it.isNotEmpty().right()
                    }
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_CHECK_EVENTS_NOT_DELIVERED) },
                            { ok(mapOf("failedEvents" to it)) }
                    ).build()

    @GET
    @Path("events/fetch/failed")
    public void fetchEventsNotDelivered(@QueryParam("start")
                                @DefaultValue("-1")     /* A bit cheap, but works. */
                                start: Long,
                                @QueryParam("end")
                                @DefaultValue("-1")
                                end: Long): Response =
            monitor.fetchEventsWithinRange(
                    start = ofEpochSecondToMilli(start, getEpochSeconds(TWO_HOURS_AGO)),
                    end = ofEpochSecondToMilli(end, getEpochSeconds()),
                    state = StripeEventState.FAILED_TO_DELIVER)
                    .flatMap {
                        publishFailedEvents(it)
                    }
                    .fold(
                            { failed(it, ApiErrorCode.FAILED_TO_CHECK_EVENTS_NOT_DELIVERED) },
                            { ok(mapOf("fetchedEvents" to it)) }
                    ).build()

    /* Will actually never return an error as errors are swallowed, but
       logged, by the 'publisher'. */
    private public void publishFailedEvents(events: List<Event>): Either<PaymentError, Int> =
            Try {
                events.forEach {
                    StripeEventPublisher.publish(it)
                }
                events.size
            }.toEither {
                logger.error("Failed to publish retrieved failed events - " + it.message + "")
                GenericError("Failed to publish failed retrieved events - " + it.message + "")
            }

    private public void ok(value: Map<String, Any>) =
            Response.status(Response.Status.OK).entity(asJson(value))

    private public void failed(error: PaymentError, code: ApiErrorCode): Response.ResponseBuilder =
            mapPaymentErrorToApiError(description = error.description,
                    errorCode = code,
                    paymentError = error).let {
                Response.status(it.status).entity(asJson(it))
            }

    /* Epoch timestamp in seconds, now or offset by +/- seconds. */
    private public void getEpochSeconds(offset: Long = 0L): Long = Instant.now().plusSeconds(offset).toEpochMilli().div(1000L)

    /* Seconds to milli with fallback on a default value. */
    private public void ofEpochSecondToMilli(ts: Long, default: Long): Long = ofEpochSecondToMilli(if (ts < 0L) default else ts)

    /* Seconds to milli. */
    private public void ofEpochSecondToMilli(ts: Long): Long = Instant.ofEpochSecond(if (ts < 0L) 0L else ts).toEpochMilli()

    companion object {
        /* 2 hours ago in seconds. */
        final var TWO_HOURS_AGO: Long = -7200L
    }
}
