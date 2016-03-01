package com.github.kmizu.kotbinator

fun E(): Parser<Int> = rule {
    (A() + ((s("+") / s("-")) + A()).repeat()).map { result ->
        result.second.fold(result.first, { value, pair ->
            if (pair.first == "+") value + pair.second else value - pair.second
        })
    }
}
fun A(): Parser<Int> = rule {
    (P() + ((s("*") / s("/")) + P()).repeat()).map { result ->
        result.second.fold(result.first, { value, pair ->
            if (pair.first == "*") value * pair.second else value / pair.second
        })
    }
}
fun P(): Parser<Int> = rule { (s("(") seqr E() seql s(")")) / numeric() }
fun alphabet(): Parser<String> = rule { r('a','z') / r('A', 'Z') }
fun identifier(): Parser<String> = rule { alphabet().repeat1().map { a: List<String> -> a.fold("", { x, y -> x + y})} }
fun numeric(): Parser<Int> = rule { r('0', '9').repeat1().map {v ->  v.fold("", {x ,y -> x + y}).toInt() } }

fun MinCSV(): Parser<List<String>> = rule{ identifier() rep1sep s(",") }

fun main(args: Array<String>) {
    val calculator = E() seql eof()
    println(calculator.parse("1"))
    println(calculator.parse("1+2"))
    println(calculator.parse("1+2*3"))
    println(calculator.parse("1+2*3/4"))
    println(calculator.parse("(1+2*3)/4"))
    val mincsv = MinCSV()
    println(mincsv.parse("A,B,C"))
    println(mincsv.parse("foo,bar"))
    println(mincsv.parse("hoge,piyo"))
    println(mincsv.parse("__,_"))
}