package it.unibo.ledsonardemo1.actors

import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.annotations.*

@QActor("ctxLedSonarAutoQAbFsmDemo")
class AppActor : AutoQActorBasicFsm() {

    override fun getBody(): ActorBasicFsm.() -> Unit {
        return {

            state("begin") {
                action {
                    actorPrintln("Started")
                }
                transition(edgeName = "t0", targetState = "work", cond = doswitch())
            }

            state("work") {
                action {
                    actorPrintln("Idle...")
                }
                transition(edgeName = "t1", targetState = "handleDistanceAlarm",
                    cond = whenEvent("distanceAlarm"))
            }

            state("handleDistanceAlarm") {
                action {
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
                transition(edgeName = "t2", "work", cond = doswitch())
            }

        }
    }

    override fun getInitialState(): String {
       return "begin"
    }

}