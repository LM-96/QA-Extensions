package it.unibo.ledsonardemo0.actors

import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.annotations.*

@QActor("ctxLedSonarAutoQAbFsmDemo")
class DistanceActor : AutoQActorBasicFsm() {

    private var currDist = -1
    private var treshold = 2000
    private var underTreshold = false

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
                transition(edgeName = "t1", targetState = "updateTreshold", cond = whenEvent("newTreshold"))
                transition(edgeName = "t2", targetState = "handleDistance", cond = whenEvent("sonarDistance"))
            }

            state("updateTreshold") {
                action {
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
                transition(edgeName = "t3", targetState = "checkCritical", cond = doswitch())
            }

            state("handleDistance") {
                action {
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
                transition(edgeName = "t4", targetState = "checkCritical", cond = doswitch())
            }

            state("checkCritical") {
                action {
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
                transition(edgeName = "t5", targetState = "work", cond = doswitch())
            }
        }
    }

    override fun getInitialState(): String {
        return "begin"
    }
}