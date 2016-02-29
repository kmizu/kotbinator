package com.github.kmizu.kotbinator

inline fun <T> block(body: () -> T): T {
    return body()
}
