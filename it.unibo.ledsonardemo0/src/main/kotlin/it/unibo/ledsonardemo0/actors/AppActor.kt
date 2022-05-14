package it.unibo.ledsonardemo0.actors

import alice.tuprolog.Term
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.annotations.*
import kotlinx.coroutines.CoroutineScope

@QActor("ctxLedSonarAbFsmDemo")
class AppActor(name : String, scope : CoroutineScope) : ActorBasicFsm(name, scope, autoStart = false) {

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
                        if(checkMsgContent(
                                Term.createTerm("distanceAlarm(TYPE)"),
                                Term.createTerm("distanceAlarm(TYPE)"),
                                currentMsg.msgContent())) {
                            when(val distanceAlarmArg = payloadArg(0)) {
                                "NORMAL", "normal" ->  {
                                    forward("ledCmd", "ledCmd(OFF)", "ledactor")
                                }
                                "CRITICAL", "critical" -> {
                                    forward("ledCmd", "ledCmd(ON)", "ledactor")
                                }
                                else -> actorPrintln("Invalid distanceAlarm arg: $distanceAlarmArg")
                            }
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

    private fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }

}