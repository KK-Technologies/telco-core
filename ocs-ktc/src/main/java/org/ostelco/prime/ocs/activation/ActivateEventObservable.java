// Converted from Kotlin: ActivateEventObservable.kt
package org.ostelco.prime.ocs.activation

import org.ostelco.prime.activation.Activation

package org.ostelco.prime.ocs.activation

import org.ostelco.prime.activation.Activation

public class ActivateEventObservable : Activation by ActivateEventObservableSingleton

// TODO vihang: Maybe use ReactiveX instead of writing Observable pattern.
public public class ActivateEventObservableSingleton : Activation {

    private final var observers = mutableSetOf<ActivateEventObserver>()

    override public void activate(msisdn: String) {
        observers.forEach { listener -> listener(msisdn) }
    }

    public void subscribe(activateEventObserver: ActivateEventObserver) = observers.add(activateEventObserver)
}

typealias ActivateEventObserver = (String) -> Unit