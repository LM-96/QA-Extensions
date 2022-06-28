package it.unibo.kactor.demo

import it.unibo.kactor.*
import it.unibo.kactor.annotations.*
import it.unibo.kactor.annotations.QakContext
import it.unibo.kactor.annotations.State
import it.unibo.kactor.dsl.start
import it.unibo.kactor.model.TransientStartMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@QakContext("sContextTest",
    "localhost", "TCP", 9000)
@HostName("localhost")
@Tracing
class ContextConfiguration

@QActor("sContextTest")
@StartMode(TransientStartMode.MANUAL)
class DemoIQActorBasicFsm : IQActorBasicFsm by qakActorFsm(DemoIQActorBasicFsm::class.java) {

    init {
        setInstanceAndStart(this)
    }

    @Initial
    @State
    @EpsilonMove("begin2idle", "idle")
    suspend fun begin() {
        actorPrintln("started")
        actorPrintln("current params: $readOnlyParameters")
        delay(2000)
    }

    @State
    suspend fun idle() {
        actorPrintln("idle")
    }

}

fun main() {
    println("DEMO | Starting...")
    DemoIQActorBasicFsm()
    println("DEMO | Started")
    /*runBlocking {
        launchQak(this)
    }*/
}