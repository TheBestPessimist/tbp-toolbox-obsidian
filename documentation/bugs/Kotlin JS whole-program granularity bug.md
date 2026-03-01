---
created: 2026-03-01
related:
  - "[[bugs]]"
  - "[[kotlin.js.ir.output.granularity]]"
  - "[[kotlinx.serialization]]"
  - "[[Kotlin Multiplatform]]"
---

When `kotlin.js.ir.output.granularity=whole-program` is enabled in a [[Kotlin Multiplatform]] JS project using [[kotlinx.serialization]], tests fail intermittently.

**Behavior**:
- First test run: usually PASSES
- Subsequent test runs: FAIL with `SerializationException at Platform.kt:50`

**Environment where reproduced**:
- Kotlin 2.3.10
- kotlinx.serialization 1.10.0
- Gradle 9.x
- Windows 11

**Root cause**: The `whole-program` compilation mode bundles everything into a single `.js` file. This appears to break how [[kotlinx.serialization]] discovers and registers serializers at runtime. The serializers work on fresh compilation but fail on subsequent runs.

**Affects**:
- [[kotlin.test]]
- [[Kotest]]
- Any test framework (bug is in the compiler/serialization interaction, not the test framework)

**Workaround**: Manually toggle `whole-program` in `gradle.properties`:
- Comment out when running tests
- Uncomment when building for production

**Minimal reproduction**: `D:\all\work\bugs\kotlin-compiler-bug--kotlin.js.ir.output.granularity--whole-program`

**JetBrains issue**: https://youtrack.jetbrains.com/issue/KT-84633 (to be filed)

