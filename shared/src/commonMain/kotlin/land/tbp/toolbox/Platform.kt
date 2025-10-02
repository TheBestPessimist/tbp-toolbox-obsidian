package land.tbp.toolbox

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform