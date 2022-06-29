package it.unibo.kactor

import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.dsl.QActorDsl
import it.unibo.kactor.parameters.ReadableOnlyParametersOwner
import it.unibo.kactor.utils.LateSingleInit
import it.unibo.kactor.model.TransientStartMode

interface IQActorBasic : ReadableOnlyParametersOwner
{

    companion object {
        internal val IQACTOR_ISTANCES = mutableMapOf<Class<*>, IQActorBasic>()
    }

    /**
     * The name of the actor registered into the infrastructure
     */
    val name : String

    /**
     * The instance used for invoking methods by using reflection.
     * **This `val` is internally used**
     */
    val instance : LateSingleInit<IQActorBasic>

    /**
     * Returns the executable [ActorBasic] that is registered into the
     * QAK infrastructure
     * @return the executable [ActorBasic] represented by this [IQActorBasic] instance
     */
    @Throws(SecurityException::class)
    fun getActorBasic() : ActorBasic

    /**
     * **Not supported for [IQActorBasic] but only for [IQActorBasicFsm]**
     *
     *
     * Sets the [instance] of this object. This method is used in order
     * to make `this` accessible to the QAK infrastructure that has to invoke
     * the *state methods* of this instance. **This method must be used only if
     * the actor is defined by implementing this interface with the `by` kotlin keywork**.
     * For example, a correct use is:
     * ```
     * @QActor
     * class ExampleActor() : IQActorBasic by qakActor() {
     *
     *  init {
     *      setInstance(this)
     *      start()
     *  }
     *  ...
     * }
     * ```
     * Notice that the [qakActor] function **starts the QAK infrastructure** only if
     * it is not been started so, when `init` is called, then the [IQActorBasic] part
     * has already been constructed and registered. In addition, **the actors that uses this
     * mechanism must have the start mode set to [TransientStartMode.MANUAL]**
     * and they has to be started after the invocation of this [setInstance] method.
     */
    fun setInstance(instance : IQActorBasic) {
        this.instance.setIfNotAlready(instance)
    }

    /**
     * **Not supported for [IQActorBasic] but only for [IQActorBasicFsm]**
     *
     *
     * Calls the [setInstance] method then start the actor.
     * This method is equivalent to:
     * ```
     *  setInstance(this)
     *  start(this)
     * ```
     * For example, in order to define an actor implementing [IQActorBasic]
     * interface, the developer can use:
     * ```
     * @QActor
     * class ExampleActor() : IQActorBasic by qakActor() {
     *
     *  init {
     *      setInstanceAndStart(this)
     *  }
     *  ...
     * }
     * ```
     * @see setInstance
     * @see start
     */
    fun setInstanceAndStart(instance : IQActorBasicFsm) {
        setInstance(instance)
        start()
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
    fun updateResource(update : String)

    //Actor lifecycle
    /**
     * Starts this actor into the QAK infrastructure.
     * Notice that it must be already registered into the QAK
     */
    fun start()

    /**
     * Terminates this actor interrupting its execution
     */
    fun terminate()

    /**
     * Prints a [line] to the standard output formatted with the name
     * of the actor
     * @param line the line to be printed
     */
    fun actorPrintln(line : String) {
        println("\t## $name \t | $line")
    }

    /**
     * Returns a string the contains a [line] formatted with the name
     * of the actor
     * @param line the line to format
     * @return the formatted line
     */
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