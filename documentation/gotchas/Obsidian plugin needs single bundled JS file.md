---
created: 2026-03-01
related:
  - "[[gotchas]]"
  - "[[Obsidian plugin]]"
  - "[[Kotlin Multiplatform]]"
  - "[[kotlin.js.ir.output.granularity]]"
---

The [[Obsidian plugin]] requires a single `main.js` file containing all code and dependencies.

**Without bundling**: Kotlin/JS with `per-module` granularity produces multiple `.js` files:
- `shared.js`
- `kotlin-kotlin-stdlib.js`
- `kotlinx-coroutines-core.js`
- `kotlinx-serialization-*.js`
- etc.

Simply copying `shared.js` to `main.js` results in a **broken plugin** because dependencies are missing.

**Solution**: Use `kotlin.js.ir.output.granularity=whole-program` in `gradle.properties` for production builds. This makes the Kotlin compiler bundle everything into a single file.

**Caveat**: This setting causes [[Kotlin JS whole-program granularity bug]] with tests. Comment it out when running tests.
