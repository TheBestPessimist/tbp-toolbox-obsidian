---
created: 2026-03-01
related:
  - "[[tasks]]"
  - "[[Kotlin JS whole-program granularity bug]]"
  - "[[gradle.properties]]"
  - "[[kotlinx.serialization]]"
---

The Gradle build was failing intermittently - tests would pass and fail at random depending on configuration in `gradle.properties`.

**Problem**: The setting `kotlin.js.ir.output.granularity=whole-program` causes [[kotlinx.serialization]] to fail on subsequent test runs. First run usually passes, subsequent runs fail with `SerializationException`.

**Investigation approach**:
- Commented out all config in `gradle.properties`
- Ran tests 5x each (`allTests` and `jsTest`) with `--rerun-tasks`
- Uncommented settings one by one, testing after each
- Identified `kotlin.js.ir.output.granularity=whole-program` as the culprit

**What we tried that did NOT work**:
- Switching from [[Kotest]] to [[kotlin.test]] - same bug, so it's not test-framework-specific
- Removing `binaries.executable()` from the library module - tests still failed
- Using `-P` command line override for the property - Gradle reads it too early in lifecycle
- Using [[esbuild]] for bundling - worked technically but added complexity

**Decision**: Manually toggle `whole-program` in `gradle.properties` when needed:
- Comment out for running tests (tests pass reliably)
- Uncomment for production builds (single bundled output)

**Outcome**:
- Created minimal reproduction project at `D:\all\work\bugs\kotlin-compiler-bug--kotlin.js.ir.output.granularity--whole-program`
- Filed/will file bug with JetBrains
- Tests pass consistently (5/5) when `whole-program` is disabled

See [[Kotlin JS whole-program granularity bug]] for technical details.

