package it.unibo.kactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

abstract class SuspendableActorBasicFsm(qafsmname:  String,
                                        fsmscope: CoroutineScope = GlobalScope,
                                        suspended : Boolean = false,
                                        ignoreMsgWhileSuspended : Boolean = false,
                                        discardMessages : Boolean = false,
                                        confined :    Boolean = false,
                                        ioBound :     Boolean = false,
                                        channelSize : Int = 50,
                                        autoBuild : Boolean = true,
                                        autoStart : Boolean = false) :
    ActorBasicFsm(qafsmname, fsmscope, discardMessages, confined, ioBound, channelSize, autoBuild, autoStart) {

    val suspendableCore : SuspendableCore
    private val superActorBody : suspend (IApplMessage) -> Unit = {msg -> super.actorBody(msg)}

    init {
        suspendableCore = createSuspendableCore(suspended, ignoreMsgWhileSuspended)
    }

    private fun createSuspendableCore(suspended: Boolean,
                                      ignoreMsgWhileSuspended: Boolean): SuspendableCore {
        return SuspendableCore(this, superActorBody,
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