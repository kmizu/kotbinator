package com.github.kmizu.kotbinator

inline fun <T> block(body: () -> T): T {
    return body()
}

inline fun <T, U> T?.map(f: (T) -> U): U? = block {
    when(this) {
        null -> null
        else -> f(this)
    }
}

fun foo() {
    val x: String? = "FOO"
    val y: Int? = x.map { x -> 1 }
}
