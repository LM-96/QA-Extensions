package it.unibo.ledsonardemo2.actors

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*

@QActor("ctxLedSonarQAbFsmDemo")
class DistanceActor : QActorBasicFsm() {

    private var currDist = -1
    private var treshold = 2000
    private var underTreshold = false

    @Initial
    @State
    @EpsilonMove("t0", "work")
    suspend fun begin() {
        actorPrintln("Started")
    }

    @State
    @WhenEvents(
        WhenEvent("t1", "updateTreshold", "newTreshold"),
        WhenEvent("t2", "handleDistance", "sonarDistance")
    )
    suspend fun work() {
        actorPrintln("Idle...")
    }

    @State
    @EpsilonMove("t3", "checkCritical")
    suspend fun updateTreshold() {
        actorPrintln("Received command for updating treshold [$currentMsg]")
        try {
            val newTreshold = currentMessageArgs[0]
            try {
                treshold = newTreshold.toInt()
                actorPrintln("Treshold updated")
            } catch (nfe : NumberFormatException) {
                actorPrintln("Invalid treshold: $newTreshold")
            }
        } catch (e : Exception) {
            actorPrintln("Invalid message for updating treshold [$currentMsg]")
        }

    }

    @State
    @EpsilonMove("t4", "checkCritical")
    suspend fun handleDistance() {
        try {
            val currDistArg = currentMessageArgs[0]
            actorPrintln("Handling distance: $currDistArg")
            try {
                currDist = currDistArg.toInt()
            } catch (nfe : NumberFormatException) {
                actorPrintln("Invalid distance: $currDistArg")
            }
        } catch (e : Exception) {
            actorPrintln("Invalid Sonar Distance event [$currentMsg]")
            e.printStackTrace()
        }

    }

    @State
    @EpsilonMove("t5", "work")
    suspend fun checkCritical() {
        if(currDist < treshold && !underTreshold ) {
            underTreshold = true
            emit event "distanceAlarm" withArgs "CRITICAL"
            actorPrintln("Emitted event \'distanceAlarm:distanceAlarm(CRITICAL)\'")
        } else if(currDist >= treshold && underTreshold) {
            underTreshold = false
            emit event "distanceAlarm" withArgs "NORMAL"
            actorPrintln("Emitted event \'distanceAlarm:distanceAlarm(CRITICAL)\'")
        }
    }
}