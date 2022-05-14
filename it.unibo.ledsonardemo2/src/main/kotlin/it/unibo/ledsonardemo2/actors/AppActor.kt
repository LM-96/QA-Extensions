package it.unibo.ledsonardemo2.actors

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*

@QActor("ctxLedSonarQAbFsmDemo")
class AppActor : QActorBasicFsm() {

    @State
    @Initial
    @EpsilonMove("t0", "work")
    suspend fun begin() {
        actorPrintln("Started")
    }

    @State
    @WhenEvent("t1", "handleDistanceAlarm", "distanceAlarm")
    suspend fun work() {
        actorPrintln("Idle...")
    }

    @State
    @EpsilonMove("t2", "work")
    suspend fun handleDistanceAlarm() {
        try {
            when(val distanceAlarmArg = currentMessageArgs[0]) {
                "NORMAL", "normal" -> send dispatch "ledCmd" to "ledactor" withArgs "OFF"
                "CRITICAL", "critical" -> send dispatch "ledCmd" to "ledactor" withArgs "ON"
                else -> actorPrintln("Invalid distanceAlarm arg: $distanceAlarmArg")
            }

        } catch (e : Exception) {
            actorPrintln("Invalid distanceAlarm event [$currentMsg]")
        }
    }

}