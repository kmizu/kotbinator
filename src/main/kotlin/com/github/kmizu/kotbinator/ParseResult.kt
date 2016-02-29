package com.github.kmizu.kotbinator

sealed class ParseResult<out T> {
    class ParseSuccess<T>(val value: T, val rest: String): ParseResult<T>() {
        override fun equals(other: Any?): Boolean = block {
            val result = when(other) {
                is ParseSuccess<*> -> value == other.value && rest == other.rest
                else -> false
            }
            result
        }

        override fun hashCode(): Int = block {
            val v = value?.hashCode()
            return when(v) {
                null -> rest.hashCode()
                else -> v.hashCode() + rest.hashCode()
            }
        }

        override fun toString(): String = block {
            return "Success($value, $rest)"
        }
    }
    class ParseFailure(val rest: String) : ParseResult<Nothing>() {
        override fun equals(other: Any?): Boolean = block {
            when(other) {
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
