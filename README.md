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
