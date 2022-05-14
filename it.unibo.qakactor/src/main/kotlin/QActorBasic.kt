package it.unibo.kactor

import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.dsl.QActorDsl

open class QActorBasic internal constructor(actorBasic : ActorBasic? = null) {

    constructor() : this(null)

    protected lateinit var actor : ActorBasic

    init {
        if(actorBasic != null)
            actor = actorBasic
    }

    val name : String
    get() {return actor.name}

    suspend fun autoMsg(msg : IApplMessage) {
        return actor.autoMsg(msg)
    }

    suspend fun autoMsg(msgId : String, msg : String) {
        return actor.autoMsg(msgId, msg)
    }

    suspend fun sendMessageToActor( msg : IApplMessage , destName: String, conn : IConnInteraction? = null ) {
        return actor.sendMessageToActor(msg, destName, conn)
    }

    fun attemptToSendViaMqtt( ctx : QakContext, msg : IApplMessage, destName : String) : Boolean {
        return actor.attemptToSendViaMqtt(ctx, msg, destName)
    }

    suspend fun forward( msgId : String, msg: String, destName: String) {
        return actor.forward(msgId, msg, destName)
    }

    suspend fun request( msgId : String, msg: String, destActor: ActorBasic) {
        return actor.request(msgId, msg, destActor)
    }

    suspend fun request( msgId : String, msg: String, destName: String) {
        return actor.request(msgId, msg, destName)
    }

    suspend fun answer( reqId: String, msgId : String, msg: String) {
        return actor.answer(reqId, msgId, msg)
    }

    suspend fun emit( ctx: QakContext, event : IApplMessage ) {
        return actor.emit(ctx, event)
    }

    suspend fun emitWithDelay(  evId: String, evContent: String, dt : Long = 0L ) {
        return actor.emitWithDelay(evId, evContent, dt)
    }

    suspend fun emit( event : IApplMessage, avatar : Boolean = false ) {
        return actor.emit(event, avatar)
    }

    suspend fun emit( msgId : String, msg : String) {
        return actor.emit(msgId, msg)
    }

    protected fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }

    protected fun actorStringln(line: String) : String {
        return "\t## $name \t | $line"
    }

    fun updateResource(update : String) {
        actor.updateResourceRep(update)
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