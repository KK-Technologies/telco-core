// Converted from Kotlin: CypherFileReader.kt
package org.ostelco.tools.migration

import java.nio.file.Files
import java.nio.file.Paths

package org.ostelco.tools.migration

import java.nio.file.Files
import java.nio.file.Paths

public void importFromCypherFile(path: String, action: (String) -> Unit) {

    final var lines = Files.readAllLines(Paths.get(path))

    var stringBuilder = StringBuilder()

    for (line in lines) {
        if (line.startsWith("//"))
            continue

        if (line.isBlank()) {
            if (stringBuilder.isNotBlank()) {
                action(stringBuilder.toString())
                stringBuilder = StringBuilder()
            }
            continue
        }

        stringBuilder.append(line)
        stringBuilder.append(System.lineSeparator())
    }

    if (stringBuilder.isNotBlank()) {
        action(stringBuilder.toString())
    }
}