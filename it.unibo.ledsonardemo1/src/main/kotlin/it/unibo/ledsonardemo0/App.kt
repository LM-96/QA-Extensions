package it.unibo.ledsonardemo0

import it.unibo.kactor.annotations.HostName
import it.unibo.kactor.annotations.QakContext
import it.unibo.kactor.annotations.Tracing
import it.unibo.kactor.launchQak
import it.unibo.ledsonarsystem.startLedSonarSystem
import it.unibo.ledsonarsystem.utils.LedSonarSystemTrace
import kotlinx.coroutines.runBlocking

@QakContext("ctxLedSonarAutoQAbFsmDemo", "localhost", "TCP",9000)
@HostName("localhost")
class ContextConfiguration

fun main(args: Array<String>) {
    startLedSonarSystem()

    runBlocking {
        launchQak(this)
    }
}