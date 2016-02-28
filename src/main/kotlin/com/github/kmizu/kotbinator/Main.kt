package com.github.kmizu.kotbinator

fun S(): Parser<Any> = rule { ((A() seq string("c")).and() seq string("a").repeat1() seq B() seq (string("a") or string("b") or string("c")).not()).toAny() }
fun A(): Parser<Any> = rule { (string("a") seq A().option() seq string("b")).toAny() }
fun B(): Parser<Any> = rule { (string("b") seq B().option() seq string("c")).toAny() }


fun main(args: Array<String>) {
    val parser = S()
    println(parser.parse("a"))
    println(parser.parse("b"))
    println(parser.parse("c"))
    println(parser.parse("abc"))
    println(parser.parse("aabbcc"))
    println(parser.parse("aaabbbccc"))
}
