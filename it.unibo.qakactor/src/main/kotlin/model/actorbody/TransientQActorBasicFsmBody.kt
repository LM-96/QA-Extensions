package it.unibo.kactor.model.actorbody

import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.QActorBasic
import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.TimerActor
import it.unibo.kactor.builders.TransitionType
import it.unibo.kactor.model.TransientState

class TransientQActorBasicFsmBody (
    initialState : String,
    states : Map<String, TransientState>,
    val qActorBasicFsm: QActorBasicFsm = QActorBasicFsm()
) : TransientActorBasicFsmBody(generateActorBasicFsmBody(states), initialState) {

    companion object {
        fun generateActorBasicFsmBody(states : Map<String, TransientState>) : ActorBasicFsm.() -> Unit {
            return {
                for(s in states) {
                    val tBody = s.value.stateBody as TransientLambdaStateBody
                    var timerDesc : Triple<String, String, Long>? = null
                    state(s.key) {
                        //action(tBody.action)
                        timerDesc = null
                        for(t in s.value.transitions) {
                            when(t.transitionType) {
                                TransitionType.EPSILON_MOVE ->
                                    transition(t.edgeName, t.targetState, cond = doswitch())

                                TransitionType.EPSILON_MOVE_GUARDED -> {
                                    transition(t.edgeName, t.targetState, cond = doswitchGuarded(t.guard!!))
                                    if(t.elseTargetState != null)
                                        transition("$#else#_#for#_${t.edgeName}", t.elseTargetState,
                                            cond = doswitchGuarded { !t.guard.invoke() })
                                }

                                TransitionType.WHEN_EVENT ->
                                    transition(t.edgeName, t.targetState, cond = whenEvent(t.msgCondName!!))

                                TransitionType.WHEN_EVENT_GUARDED -> {
                                    transition(t.edgeName, t.targetState, cond = whenEventGuarded(t.msgCondName!!, t.guard!!))
                                    if(t.elseTargetState != null)
                                        transition("$#else#_#for#_${t.edgeName}", t.elseTargetState,
                                            cond = whenEventGuarded(t.msgCondName) { !t.guard.invoke() })
                                }

                                TransitionType.WHEN_DISPATCH ->
                                    transition(t.edgeName, t.targetState, cond = whenDispatch(t.msgCondName!!))

                                TransitionType.WHEN_DISPATCH_GUARDED -> {
                                    transition(t.edgeName, t.targetState, cond = whenDispatchGuarded(t.msgCondName!!, t.guard!!))
                                    if(t.elseTargetState != null)
                                        transition("$#else#_#for#_${t.edgeName}", t.targetState,
                                            cond = whenDispatchGuarded(t.msgCondName) { !t.guard.invoke() })
                                }

                                TransitionType.WHEN_REQUEST ->
                                    transition(t.edgeName, t.targetState, cond = whenRequest(t.msgCondName!!))

                                TransitionType.WHEN_REQUEST_GUARDED -> {
                                    transition(t.edgeName, t.targetState, cond = whenRequestGuarded(t.msgCondName!!, t.guard!!))
                                    if(t.elseTargetState != null)
                                        transition("$#else#_#for#_${t.edgeName}", t.targetState,
                                            cond = whenRequestGuarded(t.msgCondName){ !t.guard.invoke()})
                                }


                                TransitionType.WHEN_REPLY ->
                                    transition(t.edgeName, t.targetState, cond = whenReply(t.msgCondName!!))

                                TransitionType.WHEN_REPLY_GUARDED -> {
                                    transition(t.edgeName, t.targetState, cond = whenReplyGuarded(t.msgCondName!!, t.guard!!))
                                    if(t.elseTargetState != null)
                                        transition("$#else#_#for#_${t.edgeName}", t.targetState,
                                            cond = whenReplyGuarded(t.msgCondName) { !t.guard.invoke() })
                                }

                                TransitionType.WHEN_TIMEOUT -> {
                                    timerDesc = Triple(
                                        "timer_${s.key}",
                                        "local_tout_${name}_${s.key}",
                                        t.millis!!)
                                    transition(t.edgeName, t.targetState, cond = whenTimeout(timerDesc!!.second))
                                }
                            }
                        }
                        if(timerDesc != null) {
                            action {
                                tBody.action.invoke(it)
                                stateTimer = TimerActor(timerDesc!!.first, scope,
                                    context!!, timerDesc!!.second, timerDesc!!.third)
                            }
                        } else action(tBody.action)
                    }
                }
            }
        }
    }

    fun injectActorBasicFsm(actorBasicFsm: ActorBasicFsm) {
        val field = QActorBasic::class.java.getDeclaredField("actor")
        field.trySetAccessible()
        field.set(qActorBasicFsm, actorBasicFsm)
    }

}