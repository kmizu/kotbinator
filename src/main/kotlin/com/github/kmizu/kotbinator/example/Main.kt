package com.github.kmizu.kotbinator.example

import com.github.kmizu.kotbinator.*

val E: Parser<Int> by lazy {
    rule {
        A.chainl(
                s("+").map{op -> {pair: Pair<Int, Int> -> val (lhs, rhs) = pair; lhs + rhs}} /
                s("-").map{op -> {pair: Pair<Int, Int> -> val (lhs, rhs) = pair; lhs - rhs}}
        )
    }
}
val A: Parser<Int> by lazy {
    rule {
        P.chainl(
                s("*").map{op -> {pair: Pair<Int, Int> -> val (lhs, rhs) = pair; lhs * rhs}} /
                s("/").map{op -> {pair: Pair<Int, Int> -> val (lhs, rhs) = pair; lhs / rhs}}
        )
    }
}
val P: Parser<Int> by lazy { rule { (s("(") seqr E seql s(")")) / numeric } }
val alphabet: Parser<String> by lazy { rule { r('a', 'z') / r('A', 'Z') } }
val identifier: Parser<String> by lazy { alphabet.repeat1().map { a: List<String> -> a.fold("", { x, y -> x + y }) } }
val numeric: Parser<Int> by lazy { r('0', '9').repeat1().map { v -> v.fold("", { x, y -> x + y }).toInt() } }
val MinCSV: Parser<List<String>> by lazy { identifier rep1sep s(",") }

fun main(args: Array<String>) {
    val calculator = E seql eof()
    println(calculator.parse("1"))
    println(calculator.parse("1+2"))
    println(calculator.parse("1+2*3"))
    println(calculator.parse("1+2*3/4"))
    println(calculator.parse("(1+2*3)/4"))
    val mincsv = MinCSV
    println(mincsv.parse("A,B,C"))
    println(mincsv.parse("foo,bar"))
    println(mincsv.parse("hoge,piyo"))
    println(mincsv.parse("__,_"))
}