// Converted from Kotlin: KtsServiceFactory.kt
package org.ostelco.prime.kts.engine

import org.ostelco.prime.kts.engine.reader.TextReader

package org.ostelco.prime.kts.engine

import org.ostelco.prime.kts.engine.reader.TextReader

public public class KtsServiceFactory {
    private String private serviceInterface;
    private TextReader private textReader;

    public KtsServiceFactory(String private serviceInterface, TextReader private textReader) {
        this.private serviceInterface = private serviceInterface;
        this.private textReader = private textReader;
    }

    public String getPrivate serviceinterface() {
        return private serviceInterface;
    }

    public void setPrivate serviceinterface(String private serviceInterface) {
        this.private serviceInterface = private serviceInterface;
    }

    public TextReader getPrivate textreader() {
        return private textReader;
    }

    public void setPrivate textreader(TextReader private textReader) {
        this.private textReader = private textReader;
    }

} {

    fun <T> getKtsService(): T = KtScriptProxy.newInstance(Class.forName(serviceInterface), textReader) as T
}