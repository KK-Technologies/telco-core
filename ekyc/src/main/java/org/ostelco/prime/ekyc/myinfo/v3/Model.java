// Converted from Kotlin: Model.kt
package org.ostelco.prime.ekyc.myinfo.v3

import com.fasterxml.jackson.annotation.JsonProperty

package org.ostelco.prime.ekyc.myinfo.v3

import com.fasterxml.jackson.annotation.JsonProperty

open public class DataItem {
    var source: String = ""
    var classification: String = ""
    @JsonProperty("lastupdated") var lastUpdated: String = ""
    var unavailable: Boolean = false
}

public class ValueDataItem(
        final var value: String
) : DataItem()

public public class PersonData {
    private ValueDataItem name;

    public PersonData(ValueDataItem name) {
        this.name = name;
    }

    public ValueDataItem getName() {
        return name;
    }

    public void setName(ValueDataItem name) {
        this.name = name;
    }

} final var dateOfBirth: Optional<ValueDataItem>,
        @JsonProperty("passexpirydate") final var passExpiryDate: Optional<ValueDataItem>
)