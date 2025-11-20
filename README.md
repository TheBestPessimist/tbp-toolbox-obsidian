### Project Structure

```
tbp-toolbox
├── obsidian-api        external declarations (@file:JsModule("obsidian"))
│
├── obsidian-fake       Implementations only; used for tests now depends on obsidian-api; @JsExport dummy classes
│
└── main project
    ├── jsMain          depends on obsidian-api
    └── jsTest          depends on obsidian-fake
```


Agent memories:
```markdown
# Kotlin/JS Development Preferences
- User prefers 100% pure Kotlin implementations without using js() calls or JavaScript/TypeScript code in the Kotlin/JS Obsidian plugin codebase.

# Project Information
- The obsidian.d.ts file with all Obsidian API declarations is located at obsidian.d.ts

# Command Line Usage
- User runs tests in CLI using git bash with the command './gradlew alltests' from the project root folder.
- To rebuild or test this Obsidian Kotlin plugin in, run: ./gradlew buildPluginProduction
- MUST **NOT** start `bash`, or `cmd`, or `pwsh`, just run the gradle command directly.
```
