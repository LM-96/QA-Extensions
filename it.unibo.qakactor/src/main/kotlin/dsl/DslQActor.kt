package it.unibo.kactor.dsl

import it.unibo.kactor.QActorBasic
import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.LoadException
import it.unibo.kactor.builders.ActorBasicFsmBuilder
import it.unibo.kactor.builders.StateBuilder
import it.unibo.kactor.model.TransitentTransition
import it.unibo.kactor.builders.TransitionType

@QActorDsl
object transitions{
    @QActorDsl
    operator fun get(vararg transientTransition : TransitentTransition) : List<TransitentTransition> {
        return transientTransition.toList()
    }
}

@QActorDsl object state
@QActorDsl object initialState

@QActorDsl
class DslQActor(private val actorBuilder: ActorBasicFsmBuilder) : QActorBasic() {

    internal lateinit var stateBuilder : StateBuilder
    private set

    private var initialFound = false
    private var initialSet = false
    private var stateToBuid = false

    @QActorDsl
    @Throws(LoadException::class)
    infix fun initialState.name(name : String) : StateNameContinuation {
        if (this@DslQActor.initialFound)
            throw LoadException("Initial state is already set. Unable to set again")
        this@DslQActor.initialFound = true
        return StateNameContinuation(name)
    }

    @QActorDsl
    infix fun state.name(name : String) : StateNameContinuation {
        return StateNameContinuation(name)
    }

    @QActorDsl
    infix fun StateNameContinuation.withBody(stateBody : suspend QActorBasicFsm.() -> Unit) : StateBodyContinuation {
        if (this@DslQActor.stateToBuid) {
            this@DslQActor.stateBuilder.buildState()
        }
        this@DslQActor.stateBuilder = this@DslQActor.actorBuilder.newState()
        this@DslQActor.stateBuilder
            .addStateName(name)
            .addStateBody(stateBody)
        if (this@DslQActor.initialFound && !this@DslQActor.initialSet) {
            this@DslQActor.initialSet = true
            this@DslQActor.actorBuilder.setInitialState(name)
        }
        if(!this@DslQActor.stateToBuid)
            this@DslQActor.stateToBuid = true
        return StateBodyContinuation
    }

    @QActorDsl
    infix fun StateBodyContinuation.with(transitions: List<TransitentTransition>) {
        this@DslQActor.stateBuilder.addTransitions(transitions)
        //this@DslQActor.stateBuilder.buildState()
    }

    @QActorDsl
    operator fun String.unaryMinus() : String {
        return this
    }

    /* TRANSITIONS */
    @QActorDsl
    infix fun String.whenDispatch(msgId : String) : TransitionNameContinuation {
        return TransitionNameContinuation(this, msgId, TransitionType.WHEN_DISPATCH)
    }

    @QActorDsl
    infix fun String.whenRequest(msgId : String) : TransitionNameContinuation {
        return TransitionNameContinuation(this, msgId, TransitionType.WHEN_REQUEST)
    }

    @QActorDsl
    infix fun String.whenReply(msgId : String) : TransitionNameContinuation {
        return TransitionNameContinuation(this, msgId, TransitionType.WHEN_REPLY)
    }

    @QActorDsl
    infix fun String.whenEvent(msgId : String) : TransitionNameContinuation {
        return TransitionNameContinuation(this, msgId, TransitionType.WHEN_REPLY)
    }

    @QActorDsl
    infix fun String.withGuard(guard : () -> Boolean) : TransitionGuardContinuation {
        return TransitionGuardContinuation(this, null, TransitionType.EPSILON_MOVE_GUARDED, guard)
    }

    @QActorDsl
    infix fun TransitionNameContinuation.withGuard(guard : () -> Boolean) : TransitionGuardContinuation {
        return TransitionGuardContinuation(edgeName, msgId, transitionType.toGuarded(), guard)
    }

    @QActorDsl
    infix fun TransitionGuardContinuation.then(stateName: String) : TransitionGuardIfTrueContinuation {
        return TransitionGuardIfTrueContinuation(edgeName, stateName, msgId, transitionType.toGuarded(), guard)
    }

    @QActorDsl
    infix fun TransitionGuardIfTrueContinuation.otherwise(stateName : String) : TransitentTransition {
        return TransitentTransition(edgeName, ifTrueTarget, transitionType, msgId, guard, elseTargetState = stateName)
    }

    @QActorDsl
    infix fun TransitionGuardContinuation.goto(stateName : String) : TransitentTransition {
        return TransitentTransition(edgeName, stateName, transitionType, msgId, guard)
    }

    @QActorDsl
    infix fun TransitionNameContinuation.goto(stateName : String) : TransitentTransition {
        return TransitentTransition(edgeName, stateName, transitionType, msgId)
    }

    @QActorDsl
    infix fun String.goto(stateName : String) : TransitentTransition {
        return TransitentTransition(this, stateName, TransitionType.EPSILON_MOVE)
    }

}