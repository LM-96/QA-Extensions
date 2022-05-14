package it.unibo.kactor.builders

import it.unibo.kactor.model.TransitentTransition

enum class TransitionType {
    WHEN_EVENT, WHEN_EVENT_GUARDED, WHEN_DISPATCH, WHEN_DISPATCH_GUARDED, WHEN_REQUEST,
    WHEN_REQUEST_GUARDED, WHEN_REPLY, WHEN_REPLY_GUARDED, WHEN_TIMEOUT, EPSILON_MOVE,
    EPSILON_MOVE_GUARDED;

    fun toGuarded() : TransitionType {
        return when(this){
            WHEN_EVENT -> WHEN_EVENT_GUARDED
            WHEN_DISPATCH -> WHEN_DISPATCH_GUARDED
            WHEN_REPLY -> WHEN_REPLY_GUARDED
            WHEN_REQUEST -> WHEN_REQUEST_GUARDED
            EPSILON_MOVE -> EPSILON_MOVE_GUARDED
            else -> this
        }
    }
}

class TransitionBuilder internal constructor(private val stateBuilder: StateBuilder){

    private var edgeName: String? = null
    private var targetState: String? = null

    fun clear() : TransitionBuilder {
        this.edgeName = null
        this.targetState = null

        return this
    }

    fun addEdgeName(name : String) : TransitionBuilder {
        this.edgeName = name
        return this
    }

    fun addTargetState(targetState: String) : TransitionBuilder {
        this.targetState = targetState
        return this
    }

    @Throws(BuildException::class)
    fun buildEpsilonMove(): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.EPSILON_MOVE)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildEpsilonMoveGuarded(elseTargetState : String? = null, guard : () -> Boolean,): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.EPSILON_MOVE_GUARDED,
                guard = guard, elseTargetState = elseTargetState)
        )
        clear()
        return stateBuilder
    }


    @Throws(BuildException::class)
    fun buildWhenEvent(eventType : String): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
            edgeName!!, targetState!!, TransitionType.WHEN_EVENT, eventType)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenEventGuarded(eventType : String, elseTargetState : String? = null, guard : () -> Boolean): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_EVENT_GUARDED, eventType,
                guard, elseTargetState = elseTargetState)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenDispatch(msgName : String): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_DISPATCH, msgName)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenDispatchGuarded(msgName: String, elseTargetState : String? = null, guard : () -> Boolean): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_DISPATCH_GUARDED, msgName,
                guard, elseTargetState = elseTargetState)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenRequest(msgName : String): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_REQUEST, msgName)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenRequestGuarded(msgName: String, elseTargetState : String? = null, guard : () -> Boolean): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_REQUEST_GUARDED, msgName,
                guard, elseTargetState = elseTargetState)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenReply(msgName : String): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_REPLY, msgName)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenReplyGuarded(msgName: String, elseTargetState : String? = null, guard : () -> Boolean): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_REPLY_GUARDED, msgName,
                guard, elseTargetState = elseTargetState)
        )
        clear()
        return stateBuilder
    }

    @Throws(BuildException::class)
    fun buildWhenTimeout(millis : Long): StateBuilder {
        if(edgeName == null)
            throw BuildException("Edge name can not be null. Please call \'addEdgeName()\' before")
        if(targetState == null)
            throw BuildException("Target state can not be null. Please call \'addTargetState()\' before")
        stateBuilder.addTransition(
            TransitentTransition(
                edgeName!!, targetState!!, TransitionType.WHEN_TIMEOUT, millis=millis)
        )
        clear()
        return stateBuilder
    }

}