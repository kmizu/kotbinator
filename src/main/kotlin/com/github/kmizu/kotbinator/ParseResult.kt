package com.github.kmizu.kotbinator

sealed class ParseResult<out T> {
    class ParseSuccess<T>(val value: T, val rest: String): ParseResult<T>()
    class ParseFailure(val rest: String) : ParseResult<Nothing>()
}
