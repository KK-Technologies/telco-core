// Converted from Kotlin: TestJsonValidation.kt
package org.ostelco.jsonschema

import org.json.JSONObject
import org.json.JSONTokener
import org.junit.Assert.assertNotNull
import org.junit.Test

package org.ostelco.jsonschema

import org.json.JSONObject
import org.json.JSONTokener
import org.junit.Assert.assertNotNull
import org.junit.Test

public class TestJsonValidation {

    @Test
    public void helloWorldTest() {
        final var inputStream = this.javaClass.getResourceAsStream("/hello-world-schema.json")
        assertNotNull(inputStream)
        final var rawSchema = JSONObject(JSONTokener(inputStream))
        final var schema =  org.everit.json.schema.loader.SchemaLoader.load(rawSchema)
        schema.validate(JSONObject("{\"hello\" : \"world\"}"))
    }

    @Test
    public void eS2DownloadOrderTest() {
        final var inputStream = this.javaClass.getResourceAsStream("/es2schemas/ES2+DownloadOrder-def.json")
        assertNotNull(inputStream)
        final var rawSchema = JSONObject(JSONTokener(inputStream))
        final var schema =  org.everit.json.schema.loader.SchemaLoader.load(rawSchema)
        schema.validate(JSONObject( "{\"eid\" : \"01234567890123456789012345678901\", \"iccid\" : \"01234567890123456789\", \"profileType\" : \"Eplestang\"}"))
    }

    // TODO: This public class does not contain any actual tests of the json schema validator.  That is
    //       clearly something that should be fixed before we start believing in this code.
}