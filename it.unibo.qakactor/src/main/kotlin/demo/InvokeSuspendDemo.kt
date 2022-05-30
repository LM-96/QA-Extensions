package it.unibo.kactor.demo

import it.unibo.kactor.utils.invokeSuspend
import kotlinx.coroutines.runBlocking

class ExampleClazz(val exampleName : String) {

    suspend fun suspendWelcome(name : String) {
        println("[${exampleName}] Welcome from suspend $name")
    }
}

fun main(args : Array<String>) {
    val suspendWelcomeMethod = ExampleClazz::class.java.methods
        .find { it.name == "suspendWelcome" }
    val exampleInst = ExampleClazz("EXAMPLE NAME")
    runBlocking {
        suspendWelcomeMethod?.invokeSuspend(exampleInst, "main")
    }
}