package it.unibo.kactor

import alice.tuprolog.Term

open class QActorBasicFsm internal constructor(actorBasicFsm : ActorBasicFsm? = null) :
    QActorBasic(actorBasicFsm)
{
    constructor() : this(null)

    init {
        if(actorBasicFsm != null)
            actor = actorBasicFsm
    }

    val currentMsg : IApplMessage
        get() {return (actor as ActorBasicFsm).getCurrentMessage()}

    val currentState : String
        get() {return (actor as ActorBasicFsm).getCurrentStateName()}

    fun payloadArg(arg : Int) : String {
        return (actor as ActorBasicFsm).payloadArg(arg)
    }

    fun checkMsgContent(template : Term, curT : Term, content : String ) : Boolean{
        return (actor as ActorBasicFsm).checkMsgContent(template, curT, content)
    }

    val currentMessageArgs : Array<String>
    get() {
        val content = currentMsg.msgContent()
        return content.substring(content.indexOf('(') + 1, content.lastIndexOf(')'))
            .split(",")
            .toTypedArray()}

    fun getCurrentMessageArg(argNum : Int) : String {
        return currentMessageArgs[argNum]
    }

}