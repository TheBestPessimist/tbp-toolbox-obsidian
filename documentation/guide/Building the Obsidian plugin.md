---
created: 2026-03-01
related:
  - "[[guide]]"
  - "[[Obsidian plugin]]"
  - "[[buildPlugin]]"
  - "[[buildPluginProduction]]"
  - "[[kotlin.js.ir.output.granularity]]"
---

**Development build** (faster, not minified):
```bash
./gradlew buildPlugin
```

**Production build** (minified):
```bash
./gradlew buildPluginProduction
```

Both tasks compile Kotlin to JS and copy `shared.js` to `D:\all\notes\.obsidian\plugins\tbp-toolbox\main.js`

**Important**: For production builds, ensure `kotlin.js.ir.output.granularity=whole-program` is **enabled** in `gradle.properties`. This bundles all dependencies into a single file.

See [[Obsidian plugin needs single bundled JS file]] for why this matters.

