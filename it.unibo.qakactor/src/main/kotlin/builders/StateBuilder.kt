package it.unibo.kactor.builders

import it.unibo.kactor.*
import it.unibo.kactor.model.*
import it.unibo.kactor.model.actorbody.*
import java.lang.reflect.Method


class StateBuilder internal constructor(
    private val actorBuilder: ActorBasicFsmBuilder,
    private val qActorBasic: QActorBasicFsm
){

    private var stateName : String? = null
    private var stateBody : TransientStateBody? = null

    private var transitions = mutableListOf<TransitentTransition>()
    private val transitionBuilder = TransitionBuilder(this)

    fun clear() : StateBuilder {
        stateName = null
        stateBody = null
        transitions = mutableListOf()

        return this
    }

    fun addStateName(name : String) : StateBuilder {
        this.stateName = name
        return this
    }

    @JvmName("addLambdaStateBody")
    fun addStateBody(body : suspend State.() -> Unit) : StateBuilder {
        this.stateBody = TransientLambdaStateBody(body)

        return this
    }

    @JvmName("addQActorStateBody")
    fun addStateBody(body : suspend QActorBasicFsm.() -> Unit) : StateBuilder {
        this.stateBody = TransientQActorStateBody(body, qActorBasic)
        return this
    }

    fun addStateBody(body : TransientStateBody) : StateBuilder {
        this.stateBody = body
        return this
    }

    fun addStateBodyByQActorMethod(method : Method) : StateBuilder {
        sysUtil.traceprintln("addStateBodyByQActorMethod(${method.name}) [declaring class: ${method.declaringClass}]")
        if(!method.declaringClass.isInstance(qActorBasic))
            throw BuildException("This method is not applicable on an ${qActorBasic.javaClass.simpleName} instance")
        this.stateBody = TransientQActorMethodStateBody(method, qActorBasic)

        return this
    }

    fun newTransition() : TransitionBuilder {
        transitionBuilder.clear()
        return transitionBuilder
    }

    @Throws(BuildException::class)
    fun buildState() : ActorBasicFsmBuilder {
        if(stateName == null)
            throw BuildException("State name can not be null. Please call \'addStateName()\' before")
        if(stateBody == null)
            throw BuildException("State body can not be null. Please call \'addStateBody()\' before")

        actorBuilder.addState(TransientState(stateName!!, stateBody!!, transitions.toList()))
        clear()
        return actorBuilder
    }

    internal fun addTransition(transition: TransitentTransition) : StateBuilder {
        transitions.add(transition)
        return this
    }

    internal fun addTransitions(transitions: Collection<TransitentTransition>) : StateBuilder {
        this.transitions.addAll(transitions)
        return this
    }

}