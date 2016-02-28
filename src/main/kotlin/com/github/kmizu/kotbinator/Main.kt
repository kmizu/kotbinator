package com.github.kmizu.kotbinator

fun S(): Parser<Any> = rule { ((A() + s("c")).and() + s("a").repeat1() + B() + !(s("a") / s("b") / s("c"))).toAny() }
fun A(): Parser<Any> = rule { (s("a") + A().option() + s("b")).toAny() }
fun B(): Parser<Any> = rule { (s("b") + B().option() + s("c")).toAny() }


fun main(args: Array<String>) {
    val parser = S()
    println(parser.parse("a"))
    println(parser.parse("b"))
    println(parser.parse("c"))
    println(parser.parse("abc"))
    println(parser.parse("aabbcc"))
    println(parser.parse("aaabbbccc"))
}
