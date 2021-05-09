package domain

interface Builder<T> {
    fun build() : T
}

fun <T, B: Builder<T>> build(init: () -> B): (B.() -> Unit) -> T = { block ->
    val builder = init()
    block(builder)
    builder.build()
}
