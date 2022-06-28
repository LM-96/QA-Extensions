package it.unibo.kactor.demo

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*
import it.unibo.kactor.launchQak
import it.unibo.kactor.model.TransientStartMode
import kotlinx.coroutines.runBlocking

@QakContext("sContextTest",
    "localhost", "TCP", 9000)
@HostName("localhost")
@Tracing
class ContextConfiguration

@QActor("sContextTest")
class DemoIQActorBasicFsm : QActorBasicFsm() {

    @Initial
    @State
    @EpsilonMove("begin2idle", "idle")
    suspend fun begin() {
        actorPrintln("started")
        actorPrintln("current params: $parameters")
    }

    @State
    suspend fun idle() {
        actorPrintln("idle")
    }

}

fun main() {
    runBlocking {
        launchQak(this)
    }
}