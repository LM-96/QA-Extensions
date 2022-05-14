package it.unibo.ledsonardemo2.actors

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*
import it.unibo.ledsonarsystem.SYSTEM_LED

@QActor("ctxLedSonarQAbFsmDemo")
class LedActor : QActorBasicFsm() {

    val led = SYSTEM_LED

    @Initial
    @State
    @EpsilonMove("t0", "work")
    suspend fun begin() {
        actorPrintln("Started")
    }

    @State
    @WhenDispatch("t1", "handleLedCmd", "ledCmd")
    suspend fun work() {
        actorPrintln("Waiting for dispatches...")
    }

    @State
    @EpsilonMove("t2", "work")
    suspend fun handleLedCmd() {
        actorPrintln("Current command: $currentMsg")
        try {
            when(val ledCmdArg = currentMessageArgs[0]) {
                "OFF", "off" -> {
                    if(led.isPoweredOn()) {
                        led.powerOff()
                        actorPrintln("Led powered off")
                    }
                }
                "ON", "on" -> {
                    if(led.isPoweredOff()) {
                        led.powerOn()
                        actorPrintln("Led powered on")
                    }
                }
                else -> actorPrintln("Invalid ledCmd arg: $ledCmdArg")
            }
        } catch (e : Exception) {
            actorPrintln("Invalid led command message [$currentMsg]")
        }
    }

}