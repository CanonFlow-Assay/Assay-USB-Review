# KotlinAssay

KotlinAssay is a deterministic checker for a small functional Kotlin profile. Its scanner is a pure function over source text; the CLI is the only code that walks directories and reads files. The order domain demonstrates immutable data classes, a sealed outcome, and a pure fold.

The project pins [Kotlin `2.4.20`](https://kotlinlang.org/docs/releases.html), the current stable Kotlin release at its creation, and compiles on JDK `25`. Its only dependencies are the Kotlin standard library and test runtime; it has no telemetry.

```sh
mvn -B verify
mvn -q exec:java -Dexec.mainClass=org.canonflow.kotlinassay.MainKt -Dexec.args="check examples/clean/src/domain --json"
```

The second command requires Maven's exec plugin when run locally; CI invokes the compiled launcher directly. Read [the v0.1 contract](docs/CONTRACT.md) for the evidence boundary and [functional-kotlin](skills/functional-kotlin/SKILL.md) for authoring guidance.

Licensed under Apache-2.0.
