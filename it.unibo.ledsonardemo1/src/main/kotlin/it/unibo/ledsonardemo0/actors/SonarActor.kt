package it.unibo.ledsonardemo0.actors

import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.TimerActor
import it.unibo.kactor.annotations.*
import it.unibo.ledsonarsystem.SYSTEM_SONAR

@QActor("ctxLedSonarAutoQAbFsmDemo")
class SonarActor : AutoQActorBasicFsm() {

    var distance = -1
    var prevDistance = distance
    val sonar = SYSTEM_SONAR

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
                    stateTimer = TimerActor("timer_begin",
                        scope, context!!, "local_tout_${name}_$stateName", 2000 )
                }
                transition(edgeName = "t1", targetState = "readSonar",
                    cond = whenTimeout("local_tout_${name}_$stateName"))
                transition(edgeName = "t2", targetState = "answareWithActual",
                    cond = whenRequest("readDistance"))
            }

            state("readSonar") {
                action {
                    prevDistance = distance
                    distance = sonar.read()
                    if(prevDistance != distance) {
                        emit("sonarDistance", "sonarDistance($distance)")
                        actorPrintln("Emitted event \'sonarDistance:sonarDistance($distance)\'")
                    }
                }
                transition(edgeName = "t3", targetState = "work", cond = doswitch())
            }

            state("answareWithActual") {
                action {
                    actorPrintln("Received request: $currentMsg")
                    replyTo request "readDistance" with "readedDistance" withArgs "$distance"
                    actorPrintln("Replied with distance [$distance]")
                }
                transition(edgeName = "t4", targetState = "work", cond = doswitch())
            }
        }
    }

    override fun getInitialState(): String {
        return "begin"
    }

}