package it.unibo.kactor.suspendable

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.IApplMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope


abstract class SuspendableActorBasic(name:         String,
                            scope: CoroutineScope = GlobalScope,
                            suspended : Boolean = false,
                            ignoreMsgWhileSuspended : Boolean = false,
                            discardMessages : Boolean = false,
                            confined :    Boolean = false,
                            ioBound :     Boolean = false,
                            channelSize : Int = 50) :
    ActorBasic(name, scope, discardMessages, confined, ioBound, channelSize) {

    abstract suspend fun onMessageWhenNotSuspended(msg : IApplMessage)
    val suspendableCore : SuspendableCore

    init {
        suspendableCore = createSuspendableCore(suspended, ignoreMsgWhileSuspended)
    }

    private fun createSuspendableCore(suspended: Boolean,
                                      ignoreMsgWhileSuspended: Boolean): SuspendableCore {
        return SuspendableCore(this, this::onMessageWhenNotSuspended,
            suspended, ignoreMsgWhileSuspended)
    }

    override suspend fun actorBody(msg: IApplMessage) {
        suspendableCore.handleMessage(msg)
    }

    suspend fun suspend() {
        autoMsg(SUSPEND_MESSAGE_ID, "SYS_SUSPEND_MSG")
    }

    suspend fun resume() {
        autoMsg(RESUME_MESSAGE_ID, "SYS_RESUME_MSG")
    }

    protected fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }
}