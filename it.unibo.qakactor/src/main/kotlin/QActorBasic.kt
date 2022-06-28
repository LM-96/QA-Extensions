package it.unibo.kactor

import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.dsl.QActorDsl
import it.unibo.kactor.parameters.ReadableOnlyParametersOwner
import it.unibo.kactor.parameters.ReadableParameterMap
import it.unibo.kactor.utils.LateSingleInit
import it.unibo.kactor.utils.lateSingleInit

open class QActorBasic internal constructor(autoSetInstance : Boolean = true,
                                            actorBasic : ActorBasic? = null)
    : IQActorBasic
{

    constructor() : this(true, null)

    override val instance: LateSingleInit<IQActorBasic> = lateSingleInit()

    protected lateinit var actor : ActorBasic

    init {
        if(actorBasic != null)
            actor = actorBasic
        if(autoSetInstance)
            setInstance(this)
    }

    override val name : String
    get() {return actor.name}

    override val readOnlyParameters: ReadableParameterMap
        get() {
            try {
                return (actor as ReadableOnlyParametersOwner).readOnlyParameters
            } catch (e : Exception) {
                throw IllegalStateException("parameters are not supported by this QActor")
            }
        }

    override suspend fun autoMsg(msg : IApplMessage) {
        return actor.autoMsg(msg)
    }

    override suspend fun autoMsg(msgId : String, msg : String) {
        return actor.autoMsg(msgId, msg)
    }

    override suspend fun sendMessageToActor( msg : IApplMessage , destName: String, conn : IConnInteraction? ) {
        return actor.sendMessageToActor(msg, destName, conn)
    }

    override fun attemptToSendViaMqtt( ctx : QakContext, msg : IApplMessage, destName : String) : Boolean {
        return actor.attemptToSendViaMqtt(ctx, msg, destName)
    }

    override suspend fun forward( msgId : String, msg: String, destName: String) {
        return actor.forward(msgId, msg, destName)
    }

    override suspend fun request( msgId : String, msg: String, destActor: ActorBasic) {
        return actor.request(msgId, msg, destActor)
    }

    override suspend fun request( msgId : String, msg: String, destName: String) {
        return actor.request(msgId, msg, destName)
    }

    override suspend fun answer( reqId: String, msgId : String, msg: String) {
        return actor.answer(reqId, msgId, msg)
    }

    override suspend fun emit( ctx: QakContext, event : IApplMessage ) {
        return actor.emit(ctx, event)
    }

    override suspend fun emitWithDelay(  evId: String, evContent: String, dt : Long ) {
        return actor.emitWithDelay(evId, evContent, dt)
    }

    override suspend fun emit( event : IApplMessage, avatar : Boolean) {
        return actor.emit(event, avatar)
    }

    override suspend fun emit( msgId : String, msg : String) {
        return actor.emit(msgId, msg)
    }

    override fun terminate() {
        actor.terminate()
    }

    override fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }

    override fun actorStringln(line: String) : String {
        return "\t## $name \t | $line"
    }

    override fun updateResource(update : String) {
        actor.updateResourceRep(update)
    }
}