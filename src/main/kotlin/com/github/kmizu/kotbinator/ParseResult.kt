package com.github.kmizu.kotbinator

import com.github.kmizu.kotbinator.util.block

sealed class ParseResult<out T> {
    class ParseSuccess<T>(val value: T, val rest: String): ParseResult<T>() {
        override fun equals(other: Any?): Boolean = block {
            when (other) {
                is ParseSuccess<*> -> value == other.value && rest == other.rest
                else -> false
            }
        }

        override fun hashCode(): Int = block {
            val v = value?.hashCode()
            when (v) {
                null -> rest.hashCode()
                else -> v.hashCode() + rest.hashCode()
            }
        }

        override fun toString(): String = block {
            "Success($value, $rest)"
        }
    }
    class ParseFailure(val rest: String) : ParseResult<Nothing>() {
        override fun equals(other: Any?): Boolean = block {
            when (other) {
                is ParseFailure -> rest == other.rest
                else -> false
            }
        }

        override fun hashCode(): Int = block {
            rest.hashCode()
        }

        override fun toString(): String = block {
            "Failure($rest)"
        }
    }
}
