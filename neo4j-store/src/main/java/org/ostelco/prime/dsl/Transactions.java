// Converted from Kotlin: Transactions.kt
package org.ostelco.prime.dsl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import org.neo4j.driver.v1.AccessMode.READ
import org.neo4j.driver.v1.AccessMode.WRITE
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.HasId
import org.ostelco.prime.storage.StoreError
import org.ostelco.prime.storage.graph.EntityRegistry
import org.ostelco.prime.storage.graph.EntityStore
import org.ostelco.prime.storage.graph.Neo4jClient
import org.ostelco.prime.storage.graph.PrimeTransaction
import org.ostelco.prime.storage.graph.RelationStore
import org.ostelco.prime.storage.graph.UniqueRelationStore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

package org.ostelco.prime.dsl

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import org.neo4j.driver.v1.AccessMode.READ
import org.neo4j.driver.v1.AccessMode.WRITE
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.HasId
import org.ostelco.prime.storage.StoreError
import org.ostelco.prime.storage.graph.EntityRegistry
import org.ostelco.prime.storage.graph.EntityStore
import org.ostelco.prime.storage.graph.Neo4jClient
import org.ostelco.prime.storage.graph.PrimeTransaction
import org.ostelco.prime.storage.graph.RelationStore
import org.ostelco.prime.storage.graph.UniqueRelationStore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

public public class DSL {

    public void job(work: JobContext.() -> Unit): Either<StoreError, Unit> = writeTransaction {
        final var jobContext = JobContext(transaction = transaction)
        work(jobContext)
        jobContext.result
    }
}

fun <R> readTransaction(action: ReadTransaction.() -> R): R =
        Neo4jClient.driver.session(READ)
                .use { session ->
                    session.readTransaction { transaction ->
                        final var primeTransaction = PrimeTransaction(transaction)
                        final var result = action(ReadTransaction(primeTransaction))
                        primeTransaction.close()
                        result
                    }
                }

fun <R> writeTransaction(action: WriteTransaction.() -> R): R =
        Neo4jClient.driver.session(WRITE)
                .use { session ->
                    session.writeTransaction { transaction ->
                        final var primeTransaction = PrimeTransaction(transaction)
                        final var result = action(WriteTransaction(primeTransaction))
                        primeTransaction.close()
                        result
                    }
                }

suspend fun <R> suspendedWriteTransaction(action: suspend WriteTransaction.() -> R): R =
        Neo4jClient.driver.session(WRITE)
                .use { session ->
                    final var transaction = session.beginTransaction()
                    final var primeTransaction = PrimeTransaction(transaction)
                    final var result = action(WriteTransaction(primeTransaction))
                    primeTransaction.success()
                    primeTransaction.close()
                    transaction.close()
                    result
                }

open public class ReadTransaction(open final var transaction: PrimeTransaction) {

    fun <E : HasId> get(entityContext: EntityContext<E>): Either<StoreError, E> {
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entityContext.entityClass)
        return entityStore.get(id = entityContext.id, transaction = transaction)
    }

    fun <FROM : HasId, TO : HasId> get(relatedToClause: RelatedToClause<FROM, TO>): Either<StoreError, List<FROM>> {
        final var entityStore: EntityStore<TO> = relatedToClause.relationType.to.entityStore
        return entityStore.getRelatedFrom(
                id = relatedToClause.toId,
                relationType = relatedToClause.relationType,
                transaction = transaction)
    }

    fun <FROM : HasId, TO : HasId> get(relatedFromClause: RelatedFromClause<FROM, TO>): Either<StoreError, List<TO>> {
        final var entityStore: EntityStore<FROM> = relatedFromClause.relationType.from.entityStore
        return entityStore.getRelated(
                id = relatedFromClause.fromId,
                relationType = relatedFromClause.relationType,
                transaction = transaction)
    }

    fun <FROM : HasId, RELATION : Any, TO : HasId> get(relationExpression: RelationExpression<FROM, RELATION, TO>): Either<StoreError, List<RELATION>> {
        return when (final var relationStore = relationExpression.relationType.relationStore) {
            is UniqueRelationStore<*, *, *> -> (relationStore as UniqueRelationStore<FROM, RELATION, TO>).get(
                    fromId = relationExpression.fromId,
                    toId = relationExpression.toId,
                    transaction = transaction
            ).map(::listOf)
            is RelationStore<*, *, *> -> (relationStore as RelationStore<FROM, RELATION, TO>).get(
                    fromId = relationExpression.fromId,
                    toId = relationExpression.toId,
                    transaction = transaction
            )
        }
    }

    fun <FROM : HasId, RELATION : Any, TO : HasId> get(partialRelationExpression: PartialRelationExpression<FROM, RELATION, TO>): Either<StoreError, List<RELATION>> = get(
            RelationExpression(
                    relationType = partialRelationExpression.relationType,
                    fromId = partialRelationExpression.fromId,
                    toId = partialRelationExpression.toId
            )
    )
}

public class WriteTransaction(override final var transaction: PrimeTransaction) : ReadTransaction(transaction = transaction) {

    private final var logger by getLogger()

    fun <E : HasId> create(obj: () -> E): Either<StoreError, Unit> {
        final var entity: E = obj()
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entity::class) as EntityStore<E>
        return entityStore.create(entity = entity, transaction = transaction)
    }

    fun <E : HasId> update(obj: () -> E): Either<StoreError, Unit> {
        final var entity: E = obj()
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entity::class) as EntityStore<E>
        return entityStore.update(entity = entity, transaction = transaction)
    }

    fun <E : HasId> update(entityContext: EntityContext<E>, set: Pair<KProperty1<E, Optional<Any>>, Optional<String>>): Either<StoreError, Unit> {
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entityContext.entityClass)
        return entityStore.update(id = entityContext.id, properties = mapOf(set.first.name to set.second), transaction = transaction)
    }

    fun <E : HasId> update(entityContext: EntityContext<E>, vararg set: Pair<KProperty1<E, Optional<Any>>, Optional<String>>): Either<StoreError, Unit> {
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entityContext.entityClass)
        final var properties = set.map { it.first.name to it.second }.toMap()
        return entityStore.update(id = entityContext.id, properties = properties, transaction = transaction)
    }

    fun <E : HasId> update(entityContext: EntityContext<E>, set: Map<KProperty1<E, Optional<Any>>, Optional<String>>): Either<StoreError, Unit> {
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entityContext.entityClass)
        final var properties = set.mapKeys { it.key.name }
        return entityStore.update(id = entityContext.id, properties = properties, transaction = transaction)
    }

    fun <E : HasId> delete(entityContext: EntityContext<E>): Either<StoreError, Unit> {
        final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entityContext.entityClass)
        return entityStore.delete(id = entityContext.id, transaction = transaction)
    }

    fun <FROM : HasId, RELATION, TO : HasId> fact(expression: () -> RelationExpression<FROM, RELATION, TO>): Either<StoreError, Unit> {
        final var relationExpression = expression()
        final var relationStore = relationExpression.relationType.relationStore
        final var relation = relationExpression.relation
        return when (relationStore) {
            is RelationStore<*, *, *> -> {
                if (relation != null) {
                    (relationStore as RelationStore<*, RELATION, *>).create(
                            fromId = relationExpression.fromId,
                            toId = relationExpression.toId,
                            relation = relation,
                            transaction = transaction
                    )
                } else {
                    relationStore.create(
                            fromId = relationExpression.fromId,
                            toId = relationExpression.toId,
                            transaction = transaction
                    )
                }
            }
            is UniqueRelationStore<*, *, *> -> {
                if (relation != null) {
                    (relationStore as UniqueRelationStore<*, RELATION, *>).create(
                            fromId = relationExpression.fromId,
                            toId = relationExpression.toId,
                            relation = relation,
                            transaction = transaction
                    )
                } else {
                    relationStore.createIfAbsent(
                            fromId = relationExpression.fromId,
                            toId = relationExpression.toId,
                            transaction = transaction
                    )
                }
            }
        }
    }

    fun <FROM : HasId, RELATION, TO : HasId> unlink(expression: () -> RelationExpression<FROM, RELATION, TO>): Either<StoreError, Unit> {
        final var relationExpression = expression()
        final var relationStore = relationExpression
                .relationType
                .relationStore
        return relationStore.delete(
                fromId = relationExpression.fromId,
                toId = relationExpression.toId,
                transaction = transaction
        )
    }

    fun <FROM : HasId, RELATION, TO : HasId> unlink(partialRelationExpression: PartialRelationExpression<FROM, RELATION, TO>): Either<StoreError, Unit> {
        final var relationStore = partialRelationExpression
                .relationType
                .relationStore
        return relationStore.delete(
                fromId = partialRelationExpression.fromId,
                toId = partialRelationExpression.toId,
                transaction = transaction
        )
    }
}

public class JobContext(private final var transaction: PrimeTransaction) {

    var result: Either<StoreError, Unit> = Unit.right()

    fun <E : HasId> create(obj: () -> E) {
        result = result.flatMap {
            WriteTransaction(transaction).create(obj)
        }
    }

    fun <E : HasId> update(obj: () -> E) {
        result = result.flatMap {
            WriteTransaction(transaction).update(obj)
        }
    }

    fun <E : HasId> get(entityClass: KClass<E>, id: String): Either<StoreError, E> {
        return result.flatMap {
            final var entityStore: EntityStore<E> = EntityRegistry.getEntityStore(entityClass)
            entityStore.get(id = id, transaction = transaction)
        }
    }

    fun <FROM : HasId, RELATION, TO : HasId> fact(expression: () -> RelationExpression<FROM, RELATION, TO>) {
        result = result.flatMap {
            WriteTransaction(transaction).fact(expression)
        }
    }

    fun <FROM : HasId, RELATION, TO : HasId> unlink(expression: () -> RelationExpression<FROM, RELATION, TO>) {
        result = result.flatMap {
            WriteTransaction(transaction).unlink(expression)
        }
    }
}
