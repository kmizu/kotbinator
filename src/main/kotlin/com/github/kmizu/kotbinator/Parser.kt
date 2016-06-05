package com.github.kmizu.kotbinator
import com.github.kmizu.kotbinator.ParseResult.*
import com.github.kmizu.kotbinator.util.block

/**
 *  @return *Parser* which parses *param*
 */
fun string(param: String): Parser<String> = parserOf {input ->
    if(input.startsWith(param))
        ParseSuccess(param, input.substring(param.length))
    else
        ParseFailure(input)
}

/**
 * Shorthand for *string(param)*
 */
fun s(param: String): Parser<String> = string(param)

/**
 * @return Parser which parses a Character in from..to
 */
fun range(from: Char, to:Char): Parser<String> = parserOf{input ->
    if(input.length > 0 && input[0] in from..to) {
        ParseSuccess(input.substring(0, 1), input.substring(1))
    } else {
        ParseFailure(input)
    }
}

/**
 * Shorthand for *range(from, to)*
 */
fun r(from: Char, to: Char): Parser<String> = range(from, to)

/**
 * Evaluates *value* and succeed without consuming any input
 */
fun <T> success(value: () -> T): Parser<T> = parserOf({input ->
    ParseSuccess(value(), input)
})

/**
 * Represents End Of File
 */
fun eof(): Parser<Any> = one().not()

/**
 * Represents any character
 */
fun one(): Parser<String> = parserOf{input ->
    if(input.length > 0)
        ParseSuccess(input.substring(0, 1), input.substring(1))
    else
        ParseFailure(input)
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
            is ParseFailure -> ParseFailure(input)
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
            is ParseFailure -> ParseFailure(input)
            is ParseSuccess -> {
                val r2 = rhs.parse(r1.rest)
                when(r2) {
                    is ParseFailure -> ParseFailure(r1.rest)
                    is ParseSuccess ->
                        ParseSuccess(Pair(r1.value, r2.value), r2.rest)
                }
            }
        }
    })

    infix fun <B> seql(rhs: Parser<B>): Parser<A> = this.seq(rhs).map{it.first}

    infix fun <B> seqr(rhs: Parser<B>): Parser<B> = this.seq(rhs).map{it.second}

    fun <B> chainl(p: Parser<B>, q: Parser<(Pair<A, B>) -> A>): Parser<A> = block {
        (this + (q + p).repeat()).map{result ->
            val (x, xs) = result
            xs.fold(x) {a, result ->
                val (f, b) = result
                f(a to b)
            }
        }
    }

    infix fun chainl(q: Parser<(Pair<A, A>) -> A>): Parser<A> = block {
        this.chainl(this, q)
    }

    infix fun <B> rep1sep(sep: Parser<B>): Parser<List<A>> = block {
        (this seq (sep seqr this).repeat()).map {
            val result = mutableListOf<A>()
            result.add(it.first)
            result.addAll(it.second)
            result
        }
    }

    infix fun <B> repsep(sep: Parser<B>): Parser<List<A>> = block {
        (this seq (sep seqr this).repeat()).option().map {
            when (it) {
                null -> mutableListOf<A>()
                else -> {
                    val result = mutableListOf<A>()
                    result.add(it.first)
                    result.addAll(it.second)
                    result
                }
            }
        }
    }

    operator fun <B> plus(rhs: Parser<B>): Parser<Pair<A, B>> = this seq rhs

    operator fun not(): Parser<Any> = parserOf({input ->
        val r = parse(input)
        when(r) {
            is ParseFailure -> ParseSuccess<Any>("", input)
            is ParseSuccess -> ParseFailure(input)
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
            is ParseFailure -> ParseFailure(input)
        }
    }
}