package it.unibo.kactor

interface I1 {

    fun printStr1()

}

interface I2 {
    fun printStr2()
}

class C1(name : String) : I1 {
    var str = "[1]ciao $name"
    override fun printStr1() {
        println(str)
    }
}

class C2 : I2 {
    var str = "[2]ciao"
    override fun printStr2() {
        println(str)
    }

}

class C3 : I1 by C1("mario"), I2 by C2() {

}

fun main() {
    val c3 = C3()
    c3.printStr1()
    c3.printStr2()

    println(C3::class.java as Class<I1>)
}