// Converted from Kotlin: Neo4jExporter.kt
package org.ostelco.tools.migration

import org.neo4j.driver.v1.Transaction

package org.ostelco.tools.migration

import org.neo4j.driver.v1.Transaction

public void importFromNeo4j(txn: Transaction, handleCypher: (String) -> Unit) {

    final var sb = StringBuilder()

    run {
        final var stmtResult = txn.run("MATCH (n) RETURN n;")
        stmtResult.forEach { record ->
            final var node = record["n"].asNode()
            final var labels = node.labels().joinToString(separator = "", prefix = ":")

            final var props = node.asMap().toSortedMap().map { entry ->
                "`" + entry.key + "`: '" + entry.value + "'"
            }.joinToString(separator = ",\n")

            sb.append("CREATE (" + labels + " {" + props + "});\n\n")
        }
    }

    run {
        final var stmtResult = txn.run("MATCH (n)-[r]->(m) RETURN n,r,m;")
        stmtResult.forEach { record ->
            final var fromNode = record["n"].asNode()
            final var relation = record["r"].asRelationship()
            final var toNode = record["m"].asNode()

            final var type = relation.type()

            var props = relation.asMap().toSortedMap().map { entry ->
                "`" + entry.key + "`: '" + entry.value + "'"
            }.joinToString(separator = ",\n")

            props = if (props.isNotBlank()) {
                " {" + props + "}"
            } else {
                props
            }

            sb.append(
"""
MATCH (n:" + fromNode.labels().first() + " {id: '" + fromNode.asMap()["id"] + "'})
  WITH n
MATCH (m:" + toNode.labels().first() + " {id: '" + toNode.asMap()["id"] + "'})
CREATE (n)-[:" + type + "" + props + "]->(m);
""")
        }
    }

    handleCypher(sb.toString())
}