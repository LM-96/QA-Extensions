package it.unibo.kactor

import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.dsl.QActorDsl
import it.unibo.kactor.parameters.ReadableOnlyParametersOwner
import it.unibo.kactor.utils.LateSingleInit

interface IQActorBasic : ReadableOnlyParametersOwner
{

    companion object {
        internal val IQACTOR_ISTANCES = mutableMapOf<Class<*>, IQActorBasic>()
        internal val CLASS_INSTANCES = mutableMapOf<Class<*>, Any>()
    }

    val name : String
    val instance : LateSingleInit<IQActorBasic>

    fun setInstance(instance : IQActorBasic) {
        this.instance.setIfNotAlready(instance)
    }

    suspend fun autoMsg(msg : IApplMessage)
    suspend fun autoMsg(msgId : String, msg : String)
    suspend fun sendMessageToActor( msg : IApplMessage , destName: String, conn : IConnInteraction? )
    fun attemptToSendViaMqtt( ctx : QakContext, msg : IApplMessage, destName : String) : Boolean
    suspend fun forward( msgId : String, msg: String, destName: String)
    suspend fun request( msgId : String, msg: String, destActor: ActorBasic)
    suspend fun request( msgId : String, msg: String, destName: String)
    suspend fun answer( reqId: String, msgId : String, msg: String)
    suspend fun emit( ctx: QakContext, event : IApplMessage )
    suspend fun emitWithDelay(  evId: String, evContent: String, dt : Long )
    suspend fun emit( event : IApplMessage, avatar : Boolean)
    suspend fun emit( msgId : String, msg : String)
    fun terminate()
    fun updateResource(update : String)

    fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }

    fun actorStringln(line: String) : String {
        return "\t## $name \t | $line"
    }

    /* DSL ****************************************************************************** */
    @QActorDsl object send
    @QActorDsl object emit
    @QActorDsl object replyTo
    @QActorDsl object update

    @QActorDsl object arg {
        operator fun get(vararg args: String) : String {
            return args.joinToString(",") { it.trim() }
        }
    }

    @QActorDsl data class ReplyToMsgIdContinuation(val reqId: String)
    @QActorDsl data class ReplyWithContinuation(val reqId : String, val resId : String)
    @QActorDsl data class MessageIdContinuation(val msgId: String, val msgType : String)
    @QActorDsl data class MessageDestContinuation(val msgId: String, val msgType: String, val destination: String)
    @QActorDsl data class EventIdContinuation(val eventId : String)

    @QActorDsl
    infix fun replyTo.request(reqId : String) : ReplyToMsgIdContinuation {
        return ReplyToMsgIdContinuation(reqId)
    }

    @QActorDsl
    infix fun ReplyToMsgIdContinuation.with(msgId: String) : ReplyWithContinuation {
        return ReplyWithContinuation(reqId, msgId)
    }

    @QActorDsl
    suspend infix fun ReplyWithContinuation.withContent(content: String)  {
        answer(reqId, resId, content)
    }

    @QActorDsl
    suspend infix fun ReplyWithContinuation.withArgs(args : String) {
        this.withContent("$resId($args)")
    }

    @QActorDsl
    infix fun send.dispatch(msgId: String) : MessageIdContinuation {
        return MessageIdContinuation(msgId, "dispatch")
    }

    @QActorDsl
    infix fun send.request(msgId: String) : MessageIdContinuation {
        return MessageIdContinuation(msgId, "request")
    }

    @QActorDsl
    infix fun MessageIdContinuation.to(destination : String) : MessageDestContinuation {
        return MessageDestContinuation(msgId, msgType, destination)
    }

    @QActorDsl
    suspend infix fun MessageDestContinuation.withArgs(args : String) {
        this.withContent("$msgId($args)")
    }

    /*@QActorDsl
    infix fun MessageDestContinuation.withArg(arg : String) : MessageArgContinuation {
        val argContinuation = MessageArgContinuation(msgId, msgType, destination)
        argContinuation.args.add(arg)

        return argContinuation
    }*/

    @QActorDsl
    suspend infix fun MessageDestContinuation.withContent(content : String) {
        when(msgType) {
            "dispatch"  -> forward(msgId, content, destination)
            "request"   -> request(msgId, content, destination)
        }
    }

    @QActorDsl
    infix fun emit.event(eventId : String) : EventIdContinuation {
        return EventIdContinuation(eventId)
    }

    @QActorDsl
    suspend infix fun EventIdContinuation.withArgs(args : String) {
        this.withContent("$eventId($args)")
    }

    @QActorDsl
    suspend infix fun EventIdContinuation.withContent(content : String) {
        emit(eventId, content)
    }

    @QActorDsl
    infix fun update.resource(update : String) {
        updateResource(update)
    }

}