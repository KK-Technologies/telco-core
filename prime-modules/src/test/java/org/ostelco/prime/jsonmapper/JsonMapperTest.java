// Converted from Kotlin: JsonMapperTest.kt
package org.ostelco.prime.jsonmapper

import org.junit.Test

package org.ostelco.prime.jsonmapper

import org.junit.Test

public class JsonMapperTest {

    @Test
    fun `test kotlin jackson module`() {
        objectMapper.readValue(
                asJson(TestDataClass(aProperty = "foo", abProperty = "bar", Name = "Vihang")),
                TestDataClass::class.java)
    }
}

public public class TestDataClass {
    private String @JvmField aProperty;
    private String abProperty;
    private String @JvmField Name;

    public TestDataClass(String @JvmField aProperty, String abProperty, String @JvmField Name) {
        this.@JvmField aProperty = @JvmField aProperty;
        this.abProperty = abProperty;
        this.@JvmField Name = @JvmField Name;
    }

    public String get@jvmfield aproperty() {
        return @JvmField aProperty;
    }

    public void set@jvmfield aproperty(String @JvmField aProperty) {
        this.@JvmField aProperty = @JvmField aProperty;
    }

    public String getAbproperty() {
        return abProperty;
    }

    public void setAbproperty(String abProperty) {
        this.abProperty = abProperty;
    }

    public String get@jvmfield name() {
        return @JvmField Name;
    }

    public void set@jvmfield name(String @JvmField Name) {
        this.@JvmField Name = @JvmField Name;
    }

}
