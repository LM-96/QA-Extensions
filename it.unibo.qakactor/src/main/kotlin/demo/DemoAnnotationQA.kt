/*package it.unibo.kactor.demo

import it.unibo.kactor.*
import it.unibo.kactor.QakContext
import it.unibo.kactor.annotations.*
import kotlinx.coroutines.runBlocking

@QActor("demoCtx")
class ReceiverAB(name : String) : ActorBasic(name) {

    override suspend fun actorBody(msg: IApplMessage) {
        println("\t## $name \t| $msg")
    }

}

@QActor("demoCtx")
class ReceiverQA : QActorBasic() {

    @ActorBody
    suspend fun actorBody(msg : IApplMessage) {
        actorPrintln("Received message: $msg")
    }

}

fun main(args : Array<String>) {
    runBlocking {
        launchQak(this)
        val msgQA = MsgUtil.buildDispatch("main", "welcome", "welcome(X)", "ReceiverQA")
        val msgAB = MsgUtil.buildDispatch("main", "welcome", "welcome(X)", "ReceiverAB")
        MsgUtil.sendMsg(msgQA, sysUtil.ctxActorMap["ReceiverQA"]!!)
        MsgUtil.sendMsg(msgAB, sysUtil.ctxActorMap["ReceiverAB"]!!)
    }
}*/