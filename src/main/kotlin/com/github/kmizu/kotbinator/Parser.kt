package com.github.kmizu.kotbinator
import com.github.kmizu.kotbinator.ParseResult.*

fun string(param: String): Parser<String> = parserOf {input ->
    if(input.startsWith(param))
        ParseSuccess<String>(param, input.substring(param.length))
    else
        ParseFailure()
}

fun s(param: String): Parser<String> = string(param)

val one: Parser<String> = parserOf{input ->
    if(input.length > 0)
        ParseSuccess(input.substring(0, 1), input.substring(1))
    else
        ParseFailure()
}

fun <A> rule(p: () -> Parser<A>): Parser<A> = parserOf({input ->
    p().parse(input)
})

fun <T> parserOf(target: (String) -> ParseResult<T>): Parser<T> = Parser<T>(target)

class Parser<A>(val target: (String) -> ParseResult<A>) {
    fun parse(input: String): ParseResult<A> {
        return target(input)
    }

    fun toAny(): Parser<Any> = parserOf({input ->
        val r = parse(input)
        when(r){
            is ParseSuccess -> ParseSuccess<Any>(r.value!!, r.rest)
            is ParseFailure -> ParseFailure()
        }
    })

    infix fun or(rhs: Parser<A>): Parser<A> = parserOf({input ->
        val r: ParseResult<A> = parse(input)
        when(r) {
            is ParseSuccess -> r
            is ParseFailure -> rhs.parse(input)
        }
    })

    operator fun div(rhs: Parser<A>): Parser<A> = this or rhs

    infix fun <B> seq(rhs: Parser<B>): Parser<Pair<A, B>> = parserOf({input ->
        val r1: ParseResult<A> = parse(input)
        when(r1){
            is ParseFailure -> ParseFailure()
            is ParseSuccess -> {
                val r2 = rhs.parse(r1.rest)
                when(r2) {
                    is ParseFailure -> ParseFailure()
                    is ParseSuccess ->
                        ParseSuccess(Pair(r1.value, r2.value), r2.rest)
                }
            }
        }
    })

    operator fun <B> plus(rhs: Parser<B>): Parser<Pair<A, B>> = this seq rhs

    operator fun not(): Parser<Any> = parserOf({input ->
        val r = parse(input)
        when(r) {
            is ParseFailure -> ParseSuccess<Any>("", input)
            is ParseSuccess -> ParseFailure()
        }
    })

    fun and(): Parser<Any> = this.not().not()

    fun option(): Parser<A?> = parserOf{input ->
        val r = parse(input)
        when(r) {
            is ParseSuccess -> r
            is ParseFailure -> ParseSuccess(null, input)
        }
    }

    fun repeat(): Parser<List<A>> = parserOf{input ->
        val rs = mutableListOf<A>()
        var rest = input
        loop@
        while(true) {
            val r = parse(rest)
            when(r) {
                is ParseSuccess -> {
                    rs.add(r.value)
                    rest = r.rest
                }
                is ParseFailure -> {
                    break@loop
                }
            }
        }
        ParseSuccess<List<A>>(rs, rest)
    }

    fun repeat1(): Parser<List<A>> = this.seq(this.repeat()).map {pair ->
        val rs = mutableListOf<A>()
        rs.add(pair.first)
        rs.addAll(pair.second)
        rs
    }

    fun <B> map(f: (A) -> B): Parser<B> = parserOf{input ->
        val r1 = parse(input)
        when(r1) {
            is ParseSuccess -> ParseSuccess(f(r1.value), r1.rest)
            is ParseFailure -> ParseFailure()
        }
    }
}