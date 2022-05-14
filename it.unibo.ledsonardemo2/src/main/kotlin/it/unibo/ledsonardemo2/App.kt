package it.unibo.ledsonardemo2

import it.unibo.kactor.annotations.HostName
import it.unibo.kactor.annotations.QakContext
import it.unibo.kactor.annotations.Tracing
import it.unibo.kactor.launchQak
import it.unibo.ledsonarsystem.startLedSonarSystem
import kotlinx.coroutines.runBlocking

@QakContext("ctxLedSonarQAbFsmDemo", "localhost", "TCP",9000)
@HostName("localhost")
@Tracing
class ContextConfiguration

fun main(args: Array<String>) {
    startLedSonarSystem()

    runBlocking {
        launchQak(this)
    }
}