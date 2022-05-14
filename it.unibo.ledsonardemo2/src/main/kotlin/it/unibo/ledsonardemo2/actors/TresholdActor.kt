package it.unibo.ledsonardemo2.actors

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*
import it.unibo.ledsonarsystem.TRESHOLD
import kotlinx.coroutines.flow.first

@QActor("ctxLedSonarQAbFsmDemo")
class TresholdActor : QActorBasicFsm() {

    private var currentTreshold = 2000

    @State
    @Initial
    @EpsilonMove("t0", "work")
    suspend fun begin() {
        TRESHOLD.start()
        actorPrintln("Started")
    }

    @State
    @EpsilonMove("t1", "work")
    suspend fun work() {
        try {
            currentTreshold = TRESHOLD.valueFlow.first { it != currentTreshold }
            emit event "newTreshold" withArgs "$currentTreshold"
            actorPrintln("Emitted event \'newTreshold:newTreshold($currentTreshold)\'")
        } catch (e : Exception) {
            actorPrintln("Unable to check treshold: ${e.localizedMessage}")
        }
    }

}