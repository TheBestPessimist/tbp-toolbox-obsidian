### Project Structure

```
tbp-toolbox
├── obsidian-api        external declarations (@file:JsModule("obsidian"))
│
├── fake-obsidian       Implementations only; used for tests now depends on obsidian-api; @JsExport dummy classes
│
└── main project
    ├── jsMain          depends on obsidian-api
    └── jsTest          depends on fake-obsidian
```
