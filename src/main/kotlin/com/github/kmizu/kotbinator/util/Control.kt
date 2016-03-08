package com.github.kmizu.kotbinator.util

inline fun <T> block(body: () -> T): T {
    return body()
}

inline fun <T, U> T?.map(f: (T) -> U): U? = block {
    when (this) {
        null -> null
        else -> f(this)
    }
}
