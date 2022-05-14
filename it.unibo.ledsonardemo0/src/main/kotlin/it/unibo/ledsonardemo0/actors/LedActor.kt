package it.unibo.ledsonardemo0.actors

import alice.tuprolog.Term
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.annotations.*
import it.unibo.ledsonarsystem.SYSTEM_LED
import kotlinx.coroutines.CoroutineScope

@QActor("ctxLedSonarAbFsmDemo")
class LedActor(name : String, scope : CoroutineScope) : ActorBasicFsm(name, scope, autoStart = false) {

    val led = SYSTEM_LED

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
                transition(edgeName = "t1", targetState = "handleLedCmd", cond = whenDispatch("ledCmd"))
            }

            state("handleLedCmd") {
                action {
                    actorPrintln("Current command: $currentMsg")
                    try {
                        if(checkMsgContent(
                                Term.createTerm("ledCmd(CMD)"),
                                Term.createTerm("ledCmd(CMD)"),
                                currentMsg.msgContent()))
                        when(val ledCmdArg = payloadArg(0)) {
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
                transition(edgeName = "t2", targetState = "work", cond = doswitch())
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