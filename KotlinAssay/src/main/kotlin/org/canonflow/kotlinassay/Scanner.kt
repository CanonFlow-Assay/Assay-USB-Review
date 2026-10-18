package org.canonflow.kotlinassay

data class Rule(
    val id: String,
    val message: String,
    val forbiddenFragments: List<String>,
)

data class Finding(
    val path: String,
    val line: Int,
    val column: Int,
    val ruleId: String,
    val message: String,
) : Comparable<Finding> {
    override fun compareTo(other: Finding): Int = compareValuesBy(this, other, Finding::path, Finding::line, Finding::column, Finding::ruleId)
}

val rules: List<Rule> = listOf(
    Rule(
        id = "KTA-F001",
        message = "Expected domain failures are values, not throws or non-null assertions.",
        forbiddenFragments = listOf("throw ", "!!"),
    ),
    Rule(
        id = "KTA-F002",
        message = "Domain code does not perform named filesystem, network, or console effects.",
        forbiddenFragments = listOf("java.io", "java.net", "println(", "readText("),
    ),
    Rule(
        id = "KTA-F003",
        message = "Domain code does not declare mutable state.",
        forbiddenFragments = listOf("var "),
    ),
)

/** Inspects a source value without filesystem access or global mutation. */
fun inspect(path: String, source: String): List<Finding> = buildList {
    source.lines().forEachIndexed { offset, line ->
        rules.forEach { rule ->
            val column = rule.forbiddenFragments
                .asSequence()
                .map { fragment -> line.indexOf(fragment) }
                .firstOrNull { index -> index >= 0 }
            if (column != null) {
                add(
                    Finding(
                        path = path,
                        line = offset + 1,
                        column = column + 1,
                        ruleId = rule.id,
                        message = rule.message,
                    ),
                )
            }
        }
    }
}.sorted()

fun renderReceipt(root: String, scannedFiles: Int, findings: List<Finding>): String {
    val findingsJson = findings.joinToString(",") { finding ->
        """{"path":${json(finding.path)},"line":${finding.line},"column":${finding.column},"ruleId":${json(finding.ruleId)},"message":${json(finding.message)}}"""
    }
    val verdict = if (findings.isEmpty()) "Pass" else "Fail"
    return """{"schemaVersion":"kotlin-assay/v0.1","root":${json(root)},"scannedFiles":$scannedFiles,"verdict":"$verdict","findings":[$findingsJson]}"""
}

private fun json(value: String): String = buildString {
    append('"')
    value.forEach { character ->
        append(
            when (character) {
                '\\' -> "\\\\"
                '"' -> "\\\""
                '\n' -> "\\n"
                '\r' -> "\\r"
                '\t' -> "\\t"
                else -> if (character.isISOControl()) "\\u%04x".format(character.code) else character.toString()
            },
        )
    }
    append('"')
}
