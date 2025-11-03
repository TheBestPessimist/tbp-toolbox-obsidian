package land.tbp.toolbox

/**
 * This is an interesting hack for instantiating JS objects.
 *
 * When i type:
 * ```kotlin
 * val obj = land.tbp.toolbox.jso<MyInterface>()
 * ```
 * what it does is:
 * ```kotlin
 * val obj: MyInterface = js("{}")
 * ```
 *
 * In other words, it creates an empty JS object, which is treated by the kotlin compiler as **exactly the type that I asked for**,
 * so I still have compiler type checking and autocomplete, even though I'm creating a garbage JS typeless object.
 */
@JsName("Object")
external fun <T : Any> jso(): T


fun <T : Any> jso(block: T.() -> Unit): T = jso<T>().apply { block() }
