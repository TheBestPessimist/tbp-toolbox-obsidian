---
created: 2026-03-01
related:
  - "[[guide]]"
  - "[[Kotlin Multiplatform]]"
  - "[[Kotest]]"
  - "[[kotlin.test]]"
  - "[[Kotlin JS whole-program granularity bug]]"
---

To run tests for a Kotlin/JS module:

```bash
./gradlew :ModuleName:jsTest
./gradlew :ModuleName:allTests
```

**Important**: Before running tests, **comment out** `kotlin.js.ir.output.granularity=whole-program` in `gradle.properties`. Otherwise tests using [[kotlinx.serialization]] will be flaky. See [[Kotlin JS whole-program granularity bug]].

**Running tests multiple times** (useful for detecting flakiness):
```powershell
for ($i = 1; $i -le 5; $i++) {
    Write-Host "Run $i:"
    ./gradlew :ModuleName:jsTest --rerun-tasks
}
```

The `--rerun-tasks` flag forces re-execution even if Gradle thinks the task is up-to-date.

