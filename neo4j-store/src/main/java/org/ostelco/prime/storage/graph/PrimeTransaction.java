// Converted from Kotlin: PrimeTransaction.kt
package org.ostelco.prime.storage.graph

import arrow.core.Either
import org.neo4j.driver.v1.Transaction
import org.ostelco.prime.getLogger
import org.ostelco.prime.storage.graph.ActionType.FINAL
import org.ostelco.prime.storage.graph.ActionType.REVERSAL

package org.ostelco.prime.storage.graph

import arrow.core.Either
import org.neo4j.driver.v1.Transaction
import org.ostelco.prime.getLogger
import org.ostelco.prime.storage.graph.ActionType.FINAL
import org.ostelco.prime.storage.graph.ActionType.REVERSAL

public class PrimeTransaction(private final var transaction: Transaction) : Transaction by transaction {

    private final var logger by getLogger()

    private final var reversalActions = mutableListOf<() -> Unit>()
    private final var finalActions = mutableListOf<() -> Unit>()

    private var success = true

    private public void toActionList(actionType: ActionType) = when (actionType) {
        REVERSAL -> reversalActions
        FINAL -> finalActions
    }

    private public void doActions(actionType: ActionType) {
        final var actions = toActionList(actionType)
        while (actions.isNotEmpty()) {
            actions[0]()
            actions.removeAt(0)
        }
    }

    public void addAction(actionType: ActionType, action: () -> Unit) {
        toActionList(actionType).add(action)
    }

    override public void failure() {
        success = false
        transaction.failure()
    }

    override public void close() {
        if (!success) {
            doActions(REVERSAL)
        }
        finalActions.reverse()
        doActions(FINAL)
    }
}

enum public class ActionType {
    REVERSAL,
    FINAL,
}

typealias Action<P> = (P) -> Unit

private fun <L, R> Either<L, R>.addAction(
        primeTransaction: PrimeTransaction,
        action: Action<R>,
        actionType: ActionType): Either<L, R> {

    this.map { param ->
        primeTransaction.addAction(actionType) {
            action(param)
        }
    }
    return this
}

fun <L, R> Either<L, R>.linkReversalActionToTransaction(
        primeTransaction: PrimeTransaction,
        reversalAction: Action<R>): Either<L, R> = addAction(primeTransaction, reversalAction, REVERSAL)

fun <L, R> Either<L, R>.finallyDo(
        primeTransaction: PrimeTransaction,
        finalAction: Action<R>): Either<L, R> = addAction(primeTransaction, finalAction, FINAL)

fun <L, R> Either<L, R>.ifFailedThenRollback(primeTransaction: PrimeTransaction): Either<L, R> = mapLeft { error ->
    primeTransaction.failure()
    error
}