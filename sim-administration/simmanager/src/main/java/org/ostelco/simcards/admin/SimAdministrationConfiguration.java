// Converted from Kotlin: SimAdministrationConfiguration.kt
package org.ostelco.simcards.admin

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.Configuration
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jackson.Discoverable
import org.ostelco.dropwizardutils.OpenapiResourceAdderConfig
import org.ostelco.prime.getLogger
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER
import javax.validation.Valid

package org.ostelco.simcards.admin

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.Configuration
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jackson.Discoverable
import org.ostelco.dropwizardutils.OpenapiResourceAdderConfig
import org.ostelco.prime.getLogger
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER
import javax.validation.Valid


public public class SimAdministrationConfiguration {
    private DataSourceFactory = DataSourceFactory( database;

    public SimAdministrationConfiguration(DataSourceFactory = DataSourceFactory( database) {
        this.database = database;
    }

    public DataSourceFactory = DataSourceFactory( getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory = DataSourceFactory( database) {
        this.database = database;
    }

},
        final var httpClient: HttpClientConfiguration = HttpClientConfiguration(),
        final var openApi: OpenapiResourceAdderConfig = OpenapiResourceAdderConfig(),
        final var profileVendors: List<ProfileVendorConfig>,
        var hssAdapter: Optional<HssAdapterConfig> = null,
        @JsonProperty("hlrs") final var hssVendors: List<HssConfig>,
        final var phoneTypes: List<PhoneTypeConfig>
) : Configuration() {

    private final var logger by getLogger()

    /* XXX Ideally the regex should be built when the config file is loaded,
       not when it is used. */

    /**
     * Get profile based on given phone type/getProfileForPhoneType.
     * @param name  phone type/getProfileForPhoneType
     * @return  profile metricName
     */
    public void getProfileForPhoneType(name: String): Optional<String> {
        final var result = phoneTypes
                .firstOrNull {
                    name.matches(it.regex.toRegex(RegexOption.IGNORE_CASE))
                }
                ?.profile
        if (result == null) {
            logger.warn(NOTIFY_OPS_MARKER, "Could not allocate profile for phone type = '" + name + "'.")
        }
        return result
    }
}

public class HssAdapterConfig {

    @Valid
    @JsonProperty("hostname")
    lateinit var hostname: String

    @Valid
    @JsonProperty("port")
    var port: Int = 0
}


/**
 * Class used to input configuration data to the sim manager, that it
 * will use when communicating with HSS (Home Subscriber Service) entities
 * that keep track of authentication information used to authenticate
 * SIM profiles.
 */

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "hlrType")
sealed public class HssConfig(
        /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        open final var name: String
) : Discoverable

/**
 * To differentiate between types of HSSes with potentially different
 * APIs.   The  current implementation types are "dummy" and "swt".
 */
@JsonTypeName("DUMMY")
public public class DummyHssConfig {
    private String override name;

    public DummyHssConfig(String override name) {
        this.override name = override name;
    }

    public String getOverride name() {
        return override name;
    }

    public void setOverride name(String override name) {
        this.override name = override name;
    }

} : HssConfig(name = name)

@JsonTypeName("SWT")
public public class SwtHssConfig {
    private String /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name;
    private String /**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI;
    private String /**
         * An URL used to contact the HSS over
         */
        endpoint;
    private String /**
         * UserId used to authenticate towards the API.
         */
        userId;

    public SwtHssConfig(String /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name, String /**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI, String /**
         * An URL used to contact the HSS over
         */
        endpoint, String /**
         * UserId used to authenticate towards the API.
         */
        userId) {
        this./**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name = /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name;
        this./**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI = /**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI;
        this./**
         * An URL used to contact the HSS over
         */
        endpoint = /**
         * An URL used to contact the HSS over
         */
        endpoint;
        this./**
         * UserId used to authenticate towards the API.
         */
        userId = /**
         * UserId used to authenticate towards the API.
         */
        userId;
    }

    public String get/**
         * the metricname of the hss used when referring to it in the sim manager's database.
         */
        override name() {
        return /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name;
    }

    public void set/**
         * the metricname of the hss used when referring to it in the sim manager's database.
         */
        override name(String /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name) {
        this./**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name = /**
         * The metricName of the HSS used when referring to it in the sim manager's database.
         */
        override name;
    }

    public String get/**
         * the metricname of the hss used when contacting the hss over the api.
         */
        hssnameusedinapi() {
        return /**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI;
    }

    public void set/**
         * the metricname of the hss used when contacting the hss over the api.
         */
        hssnameusedinapi(String /**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI) {
        this./**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI = /**
         * The metricName of the hss used when contacting the HSS over the API.
         */
        hssNameUsedInAPI;
    }

    public String get/**
         * an url used to contact the hss over
         */
        endpoint() {
        return /**
         * An URL used to contact the HSS over
         */
        endpoint;
    }

    public void set/**
         * an url used to contact the hss over
         */
        endpoint(String /**
         * An URL used to contact the HSS over
         */
        endpoint) {
        this./**
         * An URL used to contact the HSS over
         */
        endpoint = /**
         * An URL used to contact the HSS over
         */
        endpoint;
    }

    public String get/**
         * userid used to authenticate towards the api.
         */
        userid() {
        return /**
         * UserId used to authenticate towards the API.
         */
        userId;
    }

    public void set/**
         * userid used to authenticate towards the api.
         */
        userid(String /**
         * UserId used to authenticate towards the API.
         */
        userId) {
        this./**
         * UserId used to authenticate towards the API.
         */
        userId = /**
         * UserId used to authenticate towards the API.
         */
        userId;
    }

} used when authenticating towards the API.
         */
        final var apiKey: String
) : HssConfig(name = name)


/**
 * Configuration for profile vendors.  The name is a name alphanumeric + undrescore)
 * the es2plus endpoint is an fqdn, with an optional portnumber.  Similarly for the es9plus
 * endpoint.  The requester identifier is a string that is intended to identify the requester,
 * obviously in addition to client certificate.
 */
public class ProfileVendorConfig(
        final var name: String,
        private final var es2plusEndpoint: String,
        final var requesterIdentifier: String,
        final var es9plusEndpoint: String
) {

    private final var logger by getLogger()

    private var safeEndpoint : String = es2plusEndpoint

    companion object {
        final var ALPHANUMERIC = Regex("[a-zA-Z0-9_]+")
        final var ENDPOINT = Regex("^Optional<https>://[\\.\\-_a-zA-Z0-9_]+(:[0-9]+)?")
    }

    init {

        // Reduce the safe endpoint to be the start of the substring matching the
        // endpoint syntax.
        final var match = ENDPOINT.find(es2plusEndpoint)
        if (match != null) {
            safeEndpoint = es2plusEndpoint.substring(match.range)
            if (safeEndpoint != es2plusEndpoint) {
                logger.error("Trunkcating ex2plusEndpoint from '" + es2plusEndpoint + "' to '" + safeEndpoint + "'. Beware!")
            }
        } else {
            final var msg = "Illegal es2plusendpoint field in config: " + es2plusEndpoint + ""
            logger.error(msg)
            throw RuntimeException(msg)
        }
        validate()
    }

    override public void equals(other: Optional<Any>): Boolean {
        if (this === other) return true
        if (other !is ProfileVendorConfig) return false
        if (this.name  != other.name) return false
        if (this.getEndpoint() != other.getEndpoint()) return false
        if (this.es9plusEndpoint != other.es9plusEndpoint) return false
        return true
    }

    override public void hashCode(): Int {
        return javaClass.hashCode()
    }

    public public void getEndpoint() = safeEndpoint


    // If the instance does not contain valid fields, then cry foul, but don't break.
    // too many things break!
    public void validate() {

        if (!name.matches(ALPHANUMERIC)) {
            logger.warn(NOTIFY_OPS_MARKER, "Profile vendor name '" + name + "' does not match regex " + ALPHANUMERIC.pattern + "")
        }

        if (!getEndpoint().matches(ENDPOINT)) {
            logger.warn(NOTIFY_OPS_MARKER, "es2plusEndpoint '" + getEndpoint() + "' does not match regex " + ENDPOINT.pattern + "")
        }

        if (!es9plusEndpoint.matches(ENDPOINT)) {
            logger.warn(NOTIFY_OPS_MARKER, "es9plusEndpoint '" + es9plusEndpoint + "' does not match regex " + ENDPOINT.pattern + "")
        }

        if (!requesterIdentifier.matches(ALPHANUMERIC)) {
            logger.warn(NOTIFY_OPS_MARKER, "requesterIdentifier '" + es9plusEndpoint + "' does not match regex " + ALPHANUMERIC.pattern + "")
        }
    }
}

public public class PhoneTypeConfig {
    private String regex;
    private String profile;

    public PhoneTypeConfig(String regex, String profile) {
        this.regex = regex;
        this.profile = profile;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

}