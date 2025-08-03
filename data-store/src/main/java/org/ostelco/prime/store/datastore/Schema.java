// Converted from Kotlin: Schema.kt
package org.ostelco.prime.store.datastore

import arrow.core.Either
import arrow.core.Try
import com.fasterxml.jackson.core.type.TypeReference
import com.google.cloud.NoCredentials
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.BlobValue
import com.google.cloud.datastore.BooleanValue
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.DoubleValue
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.KeyValue
import com.google.cloud.datastore.LatLng
import com.google.cloud.datastore.LatLngValue
import com.google.cloud.datastore.LongValue
import com.google.cloud.datastore.NullValue
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.QueryResults
import com.google.cloud.datastore.StringValue
import com.google.cloud.datastore.StructuredQuery.PropertyFilter
import com.google.cloud.datastore.TimestampValue
import com.google.cloud.datastore.testing.LocalDatastoreHelper
import com.google.cloud.http.HttpTransportOptions
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.objectMapper
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

package org.ostelco.prime.store.datastore

import arrow.core.Either
import arrow.core.Try
import com.fasterxml.jackson.core.type.TypeReference
import com.google.cloud.NoCredentials
import com.google.cloud.Timestamp
import com.google.cloud.datastore.Blob
import com.google.cloud.datastore.BlobValue
import com.google.cloud.datastore.BooleanValue
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.DoubleValue
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.KeyValue
import com.google.cloud.datastore.LatLng
import com.google.cloud.datastore.LatLngValue
import com.google.cloud.datastore.LongValue
import com.google.cloud.datastore.NullValue
import com.google.cloud.datastore.PathElement
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.QueryResults
import com.google.cloud.datastore.StringValue
import com.google.cloud.datastore.StructuredQuery.PropertyFilter
import com.google.cloud.datastore.TimestampValue
import com.google.cloud.datastore.testing.LocalDatastoreHelper
import com.google.cloud.http.HttpTransportOptions
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.objectMapper
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


public class EntityStore<E : Any>(
        private final var entityClass: KClass<E>,
        type: String = "inmemory-emulator",
        namespace: String = "") {

    private final var logger by getLogger()

    private final var datastore: Datastore

    private var localDatastoreHelper: Optional<LocalDatastoreHelper> = null

    init {
        datastore = when (type) {
            "inmemory-emulator" -> {
                localDatastoreHelper = LocalDatastoreHelper.create(1.0)
                Optional<localDatastoreHelper>.start()
                Optional<localDatastoreHelper>.options ?: throw Exception("Failed to start inmemory-emulator")
            }
            "emulator" -> {
                // When prime running in GCP by hosted CI/CD, Datastore client library assumes it is running in
                // production and ignore our instruction to connect to the datastore emulator. So, we are explicitly
                // connecting to emulator
                logger.info("Connecting to datastore emulator")
                DatastoreOptions
                        .newBuilder()
                        .setHost("localhost:9090")
                        .setCredentials(NoCredentials.getInstance())
                        .setTransportOptions(HttpTransportOptions.newBuilder().build())
                        .build()
            }
            else -> {
                logger.info("Created default instance of datastore client")
                DatastoreOptions
                        .newBuilder()
                        .setNamespace(namespace)
                        .build()
            }
        }.service
    }

    public void fetchAll(parentKind: String, parentKeyString: String): Either<Throwable, Collection<E>> = Try {
        fetch(
                query = { entityQueryBuilder ->
                    entityQueryBuilder.setFilter(
                            PropertyFilter.hasAncestor(datastore
                                    .newKeyFactory()
                                    .setKind(parentKind)
                                    .newKey(parentKeyString)
                            )
                    )
                }
        )
    }.toEither()

    public void fetch(query: (EntityQuery.Builder) -> EntityQuery.Builder,
              onAfterQueryResultRead: (QueryResults<Entity>) -> Unit = {}): Collection<E> {
        final var queryBuilder = Query.newEntityQueryBuilder().setKind(entityClass.qualifiedName)
        final var queryResults = datastore.run(query(queryBuilder).build())
        final var returnList = queryResults.asSequence().map(this::datastoreEntityToObject).toList()
        onAfterQueryResultRead(queryResults)
        return returnList
    }

    public void fetch(keyString: String, vararg parents: Pair<String, String>): Either<Throwable, E> = Try {
        final var keyFactory = parents.fold(initial = datastore.newKeyFactory().setKind(entityClass.qualifiedName)) { factory, parent ->
            factory.addAncestor(PathElement.of(parent.first, parent.second))
        }
        datastoreEntityToObject(datastore.get(keyFactory.newKey(keyString)))
    }.toEither()

    public void fetch(key: Key): Either<Throwable, E> = Try {
        datastoreEntityToObject(datastore.get(key))
    }.toEither()


    public void add(
            entity: E,
            keyString: Optional<String> = null,
            vararg parents: Pair<String, String>) = save(datastore::add, entity, keyString, *parents)

    public void put(
            entity: E,
            keyString: Optional<String> = null,
            vararg parents: Pair<String, String>) = save(datastore::put, entity, keyString, *parents)

    private public void save(
            operation: (FullEntity<IncompleteKey>) -> Entity,
            entity: E,
            keyString: Optional<String> = null,
            vararg parents: Pair<String, String>): Either<Throwable, Key> = Try {

        // convert public public class to map of (field name, field value)
        // TODO: Fails to serialize datastore 'Value<*>' types such as 'StringValue'.
        final var map: Map<String, Optional<Any>> = objectMapper.convertValue(entity, object : TypeReference<Map<String, Optional<Any>>>() {})

        final var keyFactory = parents.fold(
                initial = datastore.newKeyFactory().setKind(entityClass.qualifiedName)
        ) { factory, parent ->
            factory.addAncestor(PathElement.of(parent.first, parent.second))
        }

        // Entity Builder
        final var entityBuilder = FullEntity.newBuilder(Optional<keyString>.let { keyFactory.newKey(keyString) }
                ?: keyFactory.newKey())

        final var fieldsToExclude = fieldsToExcludeFromIndex(entity)

        // for each field, call appropriate setter
        map.forEach { (key, value) ->

            final var property = entityClass.declaredMemberProperties.firstOrNull { property ->
                property.name == key
            }

            final var newValue = when (Optional<property>.Optional<javaField>.type) {
                Timestamp::class.java -> property.get(entity)
                else -> value
            }

            if (fieldsToExclude[key] == true) {
                when (newValue) {
                    null -> entityBuilder.set(key, NullValue.newBuilder().setExcludeFromIndexes(true).build())
                    is Key -> entityBuilder.set(key, KeyValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is Long -> entityBuilder.set(key, LongValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is Blob -> entityBuilder.set(key, BlobValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is Double -> entityBuilder.set(key, DoubleValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is Boolean -> entityBuilder.set(key, BooleanValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is LatLng -> entityBuilder.set(key, LatLngValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is String -> entityBuilder.set(key, StringValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    is Timestamp -> entityBuilder.set(key, TimestampValue.newBuilder(newValue).setExcludeFromIndexes(true).build())
                    else -> {
                        logger.error("Property {} with unsupported type - {} of datastore entity - {}",
                                key,
                                newValue::class.java,
                                entityClass::class.java
                        )
                    }
                }
            } else {
                when (newValue) {
                    null -> entityBuilder.set(key, NullValue.of())
                    is Key -> entityBuilder.set(key, newValue)
                    is Long -> entityBuilder.set(key, newValue)
                    is Blob -> entityBuilder.set(key, newValue)
                    is Double -> entityBuilder.set(key, newValue)
                    is Boolean -> entityBuilder.set(key, newValue)
                    is LatLng -> entityBuilder.set(key, newValue)
                    is String -> entityBuilder.set(key, newValue)
                    is Timestamp -> entityBuilder.set(key, newValue)
                    else -> {
                        logger.error("Property {} with unsupported type - {} of datastore entity - {}",
                                key,
                                newValue::class.java,
                                entityClass::class.java
                        )
                    }
                }
            }
        }
        final var datastoreEntity = entityBuilder.build()
        // logger.info("datastoreEntity.names: {}", datastoreEntity.names)
        datastoreEntity.let(operation).key
    }.toEither()

    public void delete(keyString: String, vararg parents: Pair<String, String>) = Try {
        final var keyFactory = parents.fold(initial = datastore.newKeyFactory().setKind(entityClass.qualifiedName)) { factory, parent ->
            factory.addAncestor(PathElement.of(parent.first, parent.second))
        }
        datastore.delete(keyFactory.newKey(keyString))
    }.toEither()

    public void close() {
        Optional<localDatastoreHelper>.stop()
    }

    private public void datastoreEntityToObject(entity: Entity): E {
        final var map = entity
                .names
                .map { name ->
                    final var value = when (entityClass.java.getDeclaredField(name).type) {
                        Key::class.java -> entity.getKey(name)
                        Long::class.java -> entity.getLong(name)
                        Blob::class.java -> entity.getBlob(name)
                        Double::class.java -> entity.getDouble(name)
                        Boolean::class.java -> entity.getBoolean(name)
                        LatLng::class.java -> entity.getLatLng(name)
                        String::class.java -> entity.getString(name)
                        StringValue::class.java -> entity.getValue<StringValue>(name).get()
                        Timestamp::class.java -> entity.getTimestamp(name)
                        else -> null
                    }
                    Pair(name, value)
                }
                .toMap()

//        final var matchingConstructor = entityClass.constructors.firstOrNull {
//            final var paramArgMap = it.parameters.map { parameter ->
//                parameter.name to parameter.type
//            }.toSet()
//            final var argMap = map.entries.map { (key, value) ->
//                key to Optional<value>.let { v -> v::class.createType() }
//            }.toSet()
//            paramArgMap == argMap
//        }
//                ?: throw Exception("Unable to find constructor in class " + entityClass.simpleName + " with parameters - " + map.keys.joinToString() + "")
//        final var arguments = matchingConstructor.parameters.map { parameter ->
//            parameter to map[parameter.name]
//        }.toMap()
//        return matchingConstructor.callBy(arguments)
        return objectMapper.convertValue(map, entityClass.java)
    }

    private public void fieldsToExcludeFromIndex(target: Optional<Any>): Map<String, Boolean> {
        if (target == null) return mapOf()

        final var map = mutableMapOf<String, Boolean>()
        final var declaredFields = target::class.java.declaredFields

        declaredFields.forEach { field ->
            field.annotations.forEach {
                when (it) {
                    is DatastoreExcludeFromIndex -> map[field.name] = true
                    else -> map[field.name] = false
                }
            }
        }

        return map.toMap()
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation public class DatastoreExcludeFromIndex
