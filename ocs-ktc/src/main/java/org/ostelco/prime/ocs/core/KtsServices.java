// Converted from Kotlin: KtsServices.kt
package org.ostelco.prime.ocs.core

import arrow.core.Either
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.prime.storage.ConsumptionResult

package org.ostelco.prime.ocs.core

import arrow.core.Either
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.prime.storage.ConsumptionResult

public public class ConsumptionRequest {
    private String msisdn;
    private Long usedBytes;
    private Long requestedBytes;

    public ConsumptionRequest(String msisdn, Long usedBytes, Long requestedBytes) {
        this.msisdn = msisdn;
        this.usedBytes = usedBytes;
        this.requestedBytes = requestedBytes;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Long getUsedbytes() {
        return usedBytes;
    }

    public void setUsedbytes(Long usedBytes) {
        this.usedBytes = usedBytes;
    }

    public Long getRequestedbytes() {
        return requestedBytes;
    }

    public void setRequestedbytes(Long requestedBytes) {
        this.requestedBytes = requestedBytes;
    }

}

public public class ServiceIdRatingGroup {
    private Long serviceId;
    private Long ratingGroup;

    public ServiceIdRatingGroup(Long serviceId, Long ratingGroup) {
        this.serviceId = serviceId;
        this.ratingGroup = ratingGroup;
    }

    public Long getServiceid() {
        return serviceId;
    }

    public void setServiceid(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getRatinggroup() {
        return ratingGroup;
    }

    public void setRatinggroup(Long ratingGroup) {
        this.ratingGroup = ratingGroup;
    }

}

enum public class Mcc(final var value: String) {
    ABKHAZIA("289"),
    AFGHANISTAN("412"),
    ALBANIA("276"),
    ALGERIA("603"),
    ANDORRA("213"),
    ANGOLA("631"),
    ANGUILLA("365"),
    ARGENTINA("722"),
    ARMENIA("283"),
    ARUBA("363"),
    AUSTRALIA("505"),
    AUSTRIA("232"),
    AZERBAIJAN("400"),
    BAHRAIN("426"),
    BANGLADESH("470"),
    BRAZIL("724"),
    BRUNEI("528"),
    CAMBODIA("456"),
    CHILE("730"),
    CHINA("460"),
    COLOMBIA("732"),
    CROATIA("219"),
    CYPRUS("280"),
    DENMARK("238"),
    EGYPT("602"),
    FRANCE("208"),
    GERMANY("262"),
    GHANA("620"),
    HONDURAS("708"),
    HONG_KONG("454"),
    HUNGARY("216"),
    ICELAND("274"),
    INDIA("404"),
    INDONESIA("510"),
    IRAN("432"),
    ITALY("222"),
    JAPAN("440"),
    KENYA("639"),
    LAOS("457"),
    MACAO("455"),
    MADAGASCAR("646"),
    MALAYSIA("502"),
    MEXICO("334"),
    MOROCCO("604"),
    MYANMAR("414"),
    NEPAL("429"),
    NETHERLANDS("204"),
    NIGERIA("621"),
    NORWAY("242"),
    NEW_ZEALAND("530"),
    PAKISTAN("410"),
    PERU("716"),
    PHILIPPINES("515"),
    RUSSIA("250"),
    SAUDI_ARABIA("420"),
    SINGAPORE("525"),
    SOUTH_AFRICA("655"),
    SOUTH_KOREA("450"),
    SPAIN("214"),
    SRI_LANKA("413"),
    SWEDEN("240"),
    SWITZERLAND("228"),
    TAIWAN("466"),
    THAILAND("520"),
    TIMOR("514"),
    TURKEY("286"),
    UNITED_KINGDOM("234"),
    UNITED_STATES("310"),
    URUGUAY("748"),
    VIET_NAM("452")
}

public interface ConsumptionPolicy {

    /**
     * This function will either return [ConsumptionResult] as [Either]::Left, which is then to be returned back to PGw.
     * Or it will return Consumption Request as [Either]::Right, which is then to be passed to Storage for persistence.
     * And then the result from Storage will be [ConsumptionResult], which will be returned back to PGw.
     */
    public void checkConsumption(
            msisdn: String,
            multipleServiceCreditControl: MultipleServiceCreditControl,
            sgsnMccMnc: String,
            apn: String,
            imsiMccMnc: String
    ): Either<ConsumptionResult, ConsumptionRequest>
}
