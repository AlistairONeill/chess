package domain

interface Builder<T> {
    fun build() : T
}

fun <T, B: Builder<T>> buildUsing(init: () -> B): (B.() -> Unit) -> T = { init().apply(it).run { build() } }
