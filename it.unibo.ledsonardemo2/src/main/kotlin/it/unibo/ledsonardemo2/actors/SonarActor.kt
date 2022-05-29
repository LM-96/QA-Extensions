package it.unibo.ledsonardemo2.actors

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*
import it.unibo.ledsonarsystem.SYSTEM_SONAR

@QActor("ctxLedSonarQAbFsmDemo")
class SonarActor : QActorBasicFsm() {

    var distance = -1
    var prevDistance = distance
    val sonar = SYSTEM_SONAR

    @Initial
    @State
    @EpsilonMove("t0", "work")
    suspend fun begin() {
        actorPrintln("Started")
    }


    @State
    @WhenTime("t1", "readSonar", 2000)
    @WhenRequest("t2", "answareWithActual", "readDistance")
    suspend fun work() {
        actorPrintln("Idle...")
    }

    @State
    @EpsilonMove("t2", "work")
    suspend fun readSonar() {
        prevDistance = distance
        distance = sonar.read()
        if(prevDistance != distance) {
            emit("sonarDistance", "sonarDistance($distance)")
            actorPrintln("Emitted event \'sonarDistance:sonarDistance($distance)\'")
        }
    }

    @State
    @EpsilonMove("t3", "work")
    suspend fun answareWithActual() {
        actorPrintln("Received request: $currentMsg")
        answer("readDistance", "readedDistance", "readedDistance($distance)")
        replyTo request "readDistance" with "readedDistance" withArgs "$distance"
        actorPrintln("Replied with distance [$distance]")
    }

}