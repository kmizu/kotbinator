package com.github.kmizu.kotbinator

fun S(): Parser<Any> = rule { ((A() + s("c")).and() + s("a").repeat1() + B() + !(s("a") / s("b") / s("c"))).toAny() }
fun A(): Parser<Any> = rule { (s("a") + A().option() + s("b")).toAny() }
fun B(): Parser<Any> = rule { (s("b") + B().option() + s("c")).toAny() }
fun Alphabet(): Parser<String> = rule { r('a','z') / r('A', 'Z') }
fun Identifier(): Parser<String> = rule { Alphabet().repeat1().map {a: List<String> -> a.fold("", {x, y -> x + y})} }

fun main(args: Array<String>) {
    val csl = S()
    println(csl.parse("a"))
    println(csl.parse("b"))
    println(csl.parse("c"))
    println(csl.parse("abc"))
    println(csl.parse("aabbcc"))
    println(csl.parse("aaabbbccc"))
    val alphabet = Identifier()
    println(alphabet.parse("Hoge"))
    println(alphabet.parse("Foo"))
    println(alphabet.parse("Bar"))
    println(alphabet.parse("_"))
}
