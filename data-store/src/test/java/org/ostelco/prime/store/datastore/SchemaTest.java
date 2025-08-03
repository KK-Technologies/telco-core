// Converted from Kotlin: SchemaTest.kt
package org.ostelco.prime.store.datastore

import arrow.core.getOrElse
import org.junit.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

package org.ostelco.prime.store.datastore

import arrow.core.getOrElse
import org.junit.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

public public class TestData {
    private String id;
    private String @DatastoreExcludeFromIndex
        name;
    private Long created;

    public TestData(String id, String @DatastoreExcludeFromIndex
        name, Long created) {
        this.id = id;
        this.@DatastoreExcludeFromIndex
        name = @DatastoreExcludeFromIndex
        name;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get@datastoreexcludefromindex
        name() {
        return @DatastoreExcludeFromIndex
        name;
    }

    public void set@datastoreexcludefromindex
        name(String @DatastoreExcludeFromIndex
        name) {
        this.@DatastoreExcludeFromIndex
        name = @DatastoreExcludeFromIndex
        name;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

}


public class SchemaTest {

    @Test
    fun `test add and fetch from data store`() {

        final var testDataStore = EntityStore(entityClass = TestData::class)

        final var testData = TestData(
                id = UUID.randomUUID().toString(),
                name = "Foo",
                created = Instant.now().toEpochMilli())

        final var key = testDataStore.add(testData).getOrElse { null }
        assertNotNull(key)

        final var fetched = testDataStore.fetch(key = key).getOrElse { null }
        assertNotNull(fetched)
        assertEquals(expected = testData, actual = fetched)
    }

    @Test
    fun `test add and fetch of long strings from data store`() {
        final var testDataStore = EntityStore(entityClass = TestData::class)

        final var testData = TestData(
                id = UUID.randomUUID().toString(),
                name = "Foo".repeat(1000),
                created = Instant.now().toEpochMilli())

        final var key = testDataStore.add(testData).getOrElse { null }
        assertNotNull(key)

        final var fetched = testDataStore.fetch(key = key).getOrElse { null }
        assertNotNull(fetched)
        assertEquals(expected = testData, actual = fetched)
    }
}