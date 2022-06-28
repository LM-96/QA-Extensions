package it.unibo.kactor.suspendable

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.IApplMessage
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val SUSPEND_MESSAGE_ID = "sys_suspend_actor"
const val RESUME_MESSAGE_ID = "sys_resume_actor"

fun IApplMessage.isSuspendCommand() : Boolean {
    if(this.msgId() == SUSPEND_MESSAGE_ID)
        return true
    return false
}

fun IApplMessage.isResumeCommand() : Boolean {
    if(this.msgId() == RESUME_MESSAGE_ID)
        return true
    return false
}

fun MsgUtil.buildSuspendMessage(sender : String, dest : String) : IApplMessage {
    return buildDispatch(sender, SUSPEND_MESSAGE_ID, "SYS_SUSPEND_MSG", dest)
}

fun MsgUtil.buildResumeMessage(sender : String, dest : String) : IApplMessage {
    return buildDispatch(sender, RESUME_MESSAGE_ID, "SYS_RESUME_MSG", dest)
}

enum class SuspensionState{
    SUSPENDED, RESUMING, NORMAL_WORK
}

class SuspendableCore(
    private val owner : ActorBasic,
    private val onMessageWhenNotSuspended : suspend (IApplMessage) -> Unit,
    private var suspended : Boolean = false,
    private val ignoreMsgWhileSuspended : Boolean = false,
) {

    val tt      = "               %%% "
    private val name = owner.name
    private val mutSuspensionStateFlow : MutableStateFlow<SuspensionState>
    val suspensionStateFlow : StateFlow<SuspensionState>
    private val msgBuffer = ArrayDeque<IApplMessage>()
    private var restoredMessage : IApplMessage? = null

    init {
        val initialSuspensionState : SuspensionState = if(suspended){
            SuspensionState.SUSPENDED
        } else {
            SuspensionState.NORMAL_WORK
        }
        mutSuspensionStateFlow = MutableStateFlow(initialSuspensionState)
        suspensionStateFlow = mutSuspensionStateFlow.asStateFlow()
    }

    suspend fun handleMessage(msg : IApplMessage) {
        sysUtil.traceprintln("$tt SuspendableMessageCore $name | buffer size before msg: ${msgBuffer.size}")
        //sysUtil.traceprintln(bufferToString())

        if(suspended) {//If suspended...
            if(msg.isResumeCommand()) resume()
            else if(!msg.isSuspendCommand() && !ignoreMsgWhileSuspended) {
                if(restoredMessage == null || msg != restoredMessage) {
                    msgBuffer.add(msg)
                }
            }

        } else {//If NOT suspended
            if(msg.isSuspendCommand()) suspend()
            else if(!msg.isResumeCommand()) {//Ignoring resume command: already active

                if(restoredMessage != null) {//The actor is not waiting for a restored message

                    if(msg == restoredMessage) {//The restored message that the actor
                        // was waiting for is arrived *********************
                        restoredMessage = null
                        onMessageWhenNotSuspended.invoke(msg)
                        tryRestoreMsgFromBuff()

                    } else {//The actor is waiting for another message ****************************
                        msgBuffer.add(msg)
                        sysUtil.traceprintln("$tt SuspendableMessageCore $name | enqueued message [$msg]")
                    }

                } else {//The actor is not waiting for a restored message: normal mechanism
                    onMessageWhenNotSuspended.invoke(msg)
                }
            }
        }
        sysUtil.traceprintln("$tt SuspendableMessageCore $name | buffer size after msg: ${msgBuffer.size}")

    }

    private suspend fun resume() {
        suspended = false
        mutSuspensionStateFlow.emit(SuspensionState.RESUMING)
        println("$tt SuspendableMessageCore $name | resumed")
        if(restoredMessage != null)
            owner.autoMsg(restoredMessage!!)
        else tryRestoreMsgFromBuff()
    }

    private suspend fun tryRestoreMsgFromBuff() {
        if(msgBuffer.size > 0) {
            restoredMessage = msgBuffer.removeFirst()
            owner.autoMsg(restoredMessage!!)
            sysUtil.traceprintln("$tt SuspendableMessageCore $name | re-sent message [$restoredMessage]")
        } else {
            mutSuspensionStateFlow.emit(SuspensionState.NORMAL_WORK)
            sysUtil.traceprintln("$tt SuspendableMessageCore $name | all messages restored [$restoredMessage]")
        }
    }

    private suspend fun suspend() {
        suspended = true
        mutSuspensionStateFlow.emit(SuspensionState.SUSPENDED)
        println("$tt SuspendableMessageHandler $name | suspended")
    }

    protected fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }
}