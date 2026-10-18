# KotlinAssay v0.1 contract

KotlinAssay scans `.kt` files recursively below its selected directory. `check` exits `0` for no selected findings, `1` for a selected finding, `2` for an unreadable target, and `64` for invalid usage. Its `--json` receipt is deterministic and omits wall-clock and host data.

The initial lexical rules observe `throw` and `!!`, named file/network/console fragments, and `var`. The checker does not parse Kotlin, type-check source, resolve imports, understand comments, or model runtime behavior. A pass is not proof of purity, correctness, security, or comprehensive functional style.
