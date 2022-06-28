package it.unibo.kactor

import alice.tuprolog.Term

open class QActorBasicFsm internal constructor(autoSetInstance : Boolean = true,
                                               actorBasicFsm : ActorBasicFsm? = null) :
    QActorBasic(autoSetInstance, actorBasicFsm), IQActorBasicFsm
{
    constructor() : this(true, null)

    init {
        if(actorBasicFsm != null)
            actor = actorBasicFsm
    }

    override val currentMsg : IApplMessage
        get() {return (actor as ActorBasicFsm).getCurrentMessage()}

    override val currentState : String
        get() {return (actor as ActorBasicFsm).getCurrentStateName()}

    override fun payloadArg(arg : Int) : String {
        return (actor as ActorBasicFsm).payloadArg(arg)
    }

    override fun checkMsgContent(template : Term, curT : Term, content : String ) : Boolean{
        return (actor as ActorBasicFsm).checkMsgContent(template, curT, content)
    }

    override val currentMessageArgs : Array<String>
    get() {
        val content = currentMsg.msgContent()
        return content.substring(content.indexOf('(') + 1, content.lastIndexOf(')'))
            .split(",")
            .toTypedArray()}

    override fun getCurrentMessageArg(argNum : Int) : String {
        return currentMessageArgs[argNum]
    }

    override fun start() {
        (actor as ActorBasicFsm).start()
    }

}