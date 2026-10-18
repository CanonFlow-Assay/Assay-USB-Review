package org.canonflow.kotlinassay

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.system.exitProcess

fun main(arguments: Array<String>) {
    exitProcess(run(arguments.toList()))
}

fun run(arguments: List<String>): Int {
    val invocation = parse(arguments) ?: run {
        System.err.println("usage: kotlin-assay check <directory> [--json]")
        return 64
    }
    if (!Files.isDirectory(invocation.root)) {
        System.err.println("kotlin-assay: not a directory: ${invocation.root}")
        return 2
    }

    return try {
        val files = Files.walk(invocation.root).use { paths ->
            paths.filter { Files.isRegularFile(it) && it.extension == "kt" }
                .sorted()
                .toList()
        }
        val findings = files.flatMap { path ->
            inspect(path.toString(), Files.readString(path, StandardCharsets.UTF_8))
        }.sorted()
        if (invocation.json) {
            println(renderReceipt(invocation.root.toString(), files.size, findings))
        } else if (findings.isEmpty()) {
            println("Pass: ${files.size} Kotlin files scanned.")
        } else {
            findings.forEach { finding ->
                println("${finding.path}:${finding.line}:${finding.column} ${finding.ruleId} ${finding.message}")
            }
        }
        if (findings.isEmpty()) 0 else 1
    } catch (exception: Exception) {
        System.err.println("kotlin-assay: ${exception.message}")
        2
    }
}

private data class Invocation(val root: Path, val json: Boolean)

private fun parse(arguments: List<String>): Invocation? {
    if (arguments.size < 2 || arguments.first() != "check") return null
    val options = arguments.drop(2)
    if (options.any { it != "--json" }) return null
    return Invocation(Path.of(arguments[1]).toAbsolutePath().normalize(), "--json" in options)
}
