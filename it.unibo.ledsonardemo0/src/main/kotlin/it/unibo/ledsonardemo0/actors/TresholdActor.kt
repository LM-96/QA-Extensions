package it.unibo.ledsonardemo0.actors

import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.TimerActor
import it.unibo.kactor.annotations.*
import it.unibo.ledsonarsystem.TRESHOLD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first

@QActor("ctxLedSonarAbFsmDemo")
class TresholdActor(name : String, scope : CoroutineScope) : ActorBasicFsm(name, scope, autoStart = false) {

    private var currentTreshold = 2000

    override fun getBody(): ActorBasicFsm.() -> Unit {
        return {
            state("begin") {
                action {
                    TRESHOLD.start()
                    actorPrintln("Started")
                }
                transition(edgeName = "t0", targetState = "work", cond = doswitch())
            }

            state("work") {
                action {
                    currentTreshold = TRESHOLD.valueFlow.first { it != currentTreshold }
                    emit("newTreshold", "newTreshold($currentTreshold)")
                    actorPrintln("Emitted event \'newTreshold:newTreshold($currentTreshold)\'")
                }
                transition(edgeName = "t1", targetState = "work", cond = doswitch())
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