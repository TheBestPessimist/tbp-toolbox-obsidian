package land.tbp.toolbox

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.logger

/**
Note about Kotlin-Js logging:

1. `private val log = KotlinLogging.logger {}`

Putting this at the top of the file (outside a class) doesn't work because i cannot set a logger name,
and the 'inferred name' is something crazy like
```
at _init_properties_cycleViewState_kt__z40e06 (plugin:tbp-toolbox:8594:17)
```
If i put it _inside_ a class, then my logs have a really stupid name such as:
```
at new Companion_9 (plugin:tbp-toolbox:4045:24)
```

It looks like there is no way to win this.

2. `val log = KotlinLogging.logger("myLoggerName")` and `val log by KotlinLogging.logger()`

This doesn't work under any form. It gives the following error:
```kotlin
Plugin failure: tbp-toolbox TypeError: context.m9 is not a function
```


So it seems all i can do is put the logger in a class using option 1.

See:
- https://github.com/oshai/kotlin-logging/issues/315
- https://github.com/oshai/kotlin-logging/wiki/Multiplatform-support
- https://github.com/oshai/kotlin-logging/pull/500 - maybe this PR fixes it? There is a lot of back and forth though
 */
//
// class LoggingSetup {
//     companion object {
//         /**
//          * Usage:
//          * ```kotlin
//          * log.info { "log info" }
//          * ```
//          * Log output:
//          * ```
//          * INFO: [    at new Companion_9 (plugin:tbp-toolbox:4045:24)] log info
//          * ```
//          * ❌ This is bad because it doesn't use the correct class name
//          */
//         val log = KotlinLogging.logger {}
//         // val log = KotlinLogging.logger("ll")
//         // val log by KotlinLogging.logger()
//     }
// }

class log {
    val log = KotlinLogging.logger {}
    // val log = KotlinLogging.logger("ll")
    // val log by KotlinLogging.logger()
}
