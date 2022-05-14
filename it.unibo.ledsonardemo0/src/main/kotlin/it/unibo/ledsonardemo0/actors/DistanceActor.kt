package it.unibo.ledsonardemo0.actors

import alice.tuprolog.Term
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.annotations.*
import kotlinx.coroutines.CoroutineScope

@QActor("ctxLedSonarAbFsmDemo")
class DistanceActor(name : String, scope : CoroutineScope) : ActorBasicFsm(name, scope, autoStart = false) {

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
                        if(checkMsgContent(
                                Term.createTerm("newTreshold(TRESHOLD)"),
                                Term.createTerm("newTreshold(TRESHOLD)"),
                                currentMsg.msgContent())) {
                            val newTreshold = payloadArg(0)
                            try {
                                treshold = newTreshold.toInt()
                                actorPrintln("Treshold updated")
                            } catch (nfe : NumberFormatException) {
                                actorPrintln("Invalid treshold: $newTreshold")
                            }

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
                        if(checkMsgContent(
                                Term.createTerm("sonarDistance(DIST)"),
                                Term.createTerm("sonarDistance(DIST)"),
                                currentMsg.msgContent())) {
                            val currDistArg = payloadArg(0)
                            actorPrintln("Handling distance: $currDistArg")
                            try {
                                currDist = currDistArg.toInt()
                            } catch (nfe : NumberFormatException) {
                                actorPrintln("Invalid distance: $currDistArg")
                            }
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
                        emit("distanceAlarm", "distanceAlarm(CRITICAL)")
                        actorPrintln("Emitted event \'distanceAlarm:distanceAlarm(CRITICAL)\'")
                    } else if(currDist >= treshold && underTreshold) {
                        underTreshold = false
                        emit("distanceAlarm", "distanceAlarm(NORMAL)")
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

    private fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }
}