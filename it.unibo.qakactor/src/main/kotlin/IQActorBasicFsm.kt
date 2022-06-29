package it.unibo.kactor

import alice.tuprolog.Term

interface IQActorBasicFsm : IQActorBasic
{

    val currentMsg : IApplMessage
    val currentState : String

    override fun getActorBasic() : ActorBasicFsm
    fun payloadArg(arg : Int) : String
    fun checkMsgContent(template : Term, curT : Term, content : String ) : Boolean

    val currentMessageArgs : Array<String>
    fun getCurrentMessageArg(argNum : Int) : String

}