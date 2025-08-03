// Converted from Kotlin: KycServices.kt
package org.ostelco.prime.ekyc

import org.ostelco.prime.model.MyInfoConfig
import java.time.LocalDate

package org.ostelco.prime.ekyc

import org.ostelco.prime.model.MyInfoConfig
import java.time.LocalDate

public interface MyInfoKycService {
    public void getConfig() : MyInfoConfig
    public void getPersonData(authorisationCode: String) : Optional<MyInfoData>
}

public interface DaveKycService {
    public void validate(id: Optional<String>) : Boolean
}

public public class MyInfoData {
    private String uinFin;
    private Optional<LocalDate> birthDate;
    private Optional<LocalDate> passExpiryDate;
    private Optional<String> personData;

    public MyInfoData(String uinFin, Optional<LocalDate> birthDate, Optional<LocalDate> passExpiryDate, Optional<String> personData) {
        this.uinFin = uinFin;
        this.birthDate = birthDate;
        this.passExpiryDate = passExpiryDate;
        this.personData = personData;
    }

    public String getUinfin() {
        return uinFin;
    }

    public void setUinfin(String uinFin) {
        this.uinFin = uinFin;
    }

    public Optional<LocalDate> getBirthdate() {
        return birthDate;
    }

    public void setBirthdate(Optional<LocalDate> birthDate) {
        this.birthDate = birthDate;
    }

    public Optional<LocalDate> getPassexpirydate() {
        return passExpiryDate;
    }

    public void setPassexpirydate(Optional<LocalDate> passExpiryDate) {
        this.passExpiryDate = passExpiryDate;
    }

    public Optional<String> getPersondata() {
        return personData;
    }

    public void setPersondata(Optional<String> personData) {
        this.personData = personData;
    }

}