package it.unibo.kactor.builders

import it.unibo.kactor.AutoQActorBasicFsm
import it.unibo.kactor.IQActorBasicFsm
import it.unibo.kactor.model.*
import it.unibo.kactor.model.actorbody.TransientActorBasicBody
import it.unibo.kactor.model.actorbody.TransientActorBasicFsmBody
import it.unibo.kactor.model.actorbody.TransientAutoActorBasicFsmClassBody
import it.unibo.kactor.model.actorbody.TransientQActorBasicFsmBody

class ActorBasicFsmBuilder(contextBuilder: ContextBuilder? = null) :
    ActorBasicBuilder(contextBuilder){

    private var initialState : String? = null

    private var states : MutableMap<String, TransientState>? = null
    private var qActor : IQActorBasicFsm? = null
    private var stateBuilder : StateBuilder? = null

    //private var actorBodyFsm : TransientActorBasicFsmBody? = null


    override fun clear() : ActorBasicFsmBuilder {
        super.clear()
        initialState = null
        states = null
        stateBuilder = null

        return this
    }

    @Throws(BuildException::class)
    fun newState() : StateBuilder {
        if(qActor == null)
            throw BuildException("Unable to have a \'${StateBuilder::class.java.simpleName}\' instance with a null \'qActorBasicFsm\'. " +
                    "Please call \'addQActorBasicFsm()\' before")
        if(stateBuilder == null) {
            states = mutableMapOf()
            stateBuilder = StateBuilder(this, qActor!!)
        }

        return stateBuilder!!
    }

    fun addQActorBasicFsm(qActorBasicFsm: IQActorBasicFsm) : ActorBasicFsmBuilder {
        if(stateBuilder != null)
            throw BuildException("Unable to change the \'qActorBasicFsm\' instance because a state builder is already been required")

        this.qActor = qActorBasicFsm
        return this
    }

    fun setInitialState(name : String) : ActorBasicFsmBuilder {
        this.initialState = name
        return this
    }

    fun addActorFsmBody(body : TransientActorBasicFsmBody) : ActorBasicFsmBuilder {
        this.actorBody = body
        return this
    }

    fun addAutoActorFsmBody(clazz : Class<out AutoQActorBasicFsm>) : ActorBasicFsmBuilder {
        return addActorFsmBody(TransientAutoActorBasicFsmClassBody(clazz))
    }

    @Throws(BuildException::class)
    override fun build() : TransientActorBasicFsm {

        if(actorBody == null) {
            if(initialState == null)
                throw BuildException("Actor initial state can not be null. Please call \'setInitialState()\' before")
            if(states == null)
                throw BuildException("Please add a state or a ${TransientActorBasicFsmBody::class.java.simpleName} instance: unable to have an actor without a body")

            for(s in states!!) {
                for (t in s.value.transitions)
                    if (!states!!.containsKey(t.targetState))
                        throw BuildException("Transition  edgeName='${t.edgeName}' " +
                                "[actor=\'$actorName\', state=\'${s.key}\'] has a inexistent target " +
                                "state \'${t.targetState}\'")
            }
            super.addActorBody(TransientQActorBasicFsmBody(initialState!!, states!!, qActor!!))
        }
        //printSummary()
        val actor : TransientActorBasicFsm = super.build().asFsm()
        clear()
        return actor
    }

    @Throws(BuildException::class)
    override fun buildInContext() : Pair<TransientActorBasicFsm, ContextBuilder> {
        if(contextBuilder == null)
            throw BuildException("Unable to build inside context: this builder is not provided by a ${ContextBuilder::class.java.simpleName} instance")

        val actor = build()
        contextBuilder.addActor(actor)
        return Pair(actor, contextBuilder)
    }


    /*private fun printSummary() {
        val title = "Actor Build Summary : $actorName ***********************************************"
        var body = ""
        var end = ""
        for(t in 0..title.length)
            end += "*"
        for(s in states) {
            body += "\t state \'${s.key}\': ${s.value.stateBody}\n"
            for(t in s.value.transitions) {
                body += "\t\t transition \'${t.edgeName}\' : -> \'${t.targetState}\' [$t]\n"
            }
        }
        println("$title\n$body\n$end\n")
    }*/

    internal fun addState(state : TransientState) : ActorBasicFsmBuilder {
        states!![state.stateName] = state
        return this
    }

    @Throws(BuildException::class)
    override fun addActorBody(actorBody : TransientActorBasicBody) : ActorBasicBuilder {
        throw BuildException("This builder is for finite state machine")
    }
}