package it.unibo.kactor.builders

import it.unibo.kactor.*
import it.unibo.kactor.model.TransientActorBasic
import it.unibo.kactor.model.actorbody.*
import it.unibo.kactor.parameters.MutableParameterMap
import it.unibo.kactor.parameters.mutableParameterMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.lang.reflect.Method
import java.util.*

open class ActorBasicBuilder(protected val contextBuilder: ContextBuilder? = null) {

    protected var actorName : String? = null
    protected var suspendable : Boolean = false
    protected var actorScope : CoroutineScope = DEFAULT_SCOPE
    protected var discardMessages : Boolean = DEFAULT_DISCARD_MESSAGE
    protected var confined : Boolean = DEFAULT_CONFINED
    protected var ioBound : Boolean = DEFAULT_IO_BOUND
    protected var channelSize : Int = DEFAUL_CHANNEL_SIZE
    protected var parameterMap : MutableParameterMap = mutableParameterMap()

    protected var actorBody : TransientActorBasicBody? = null

    companion object {
        val DEFAULT_SCOPE = GlobalScope
        val DEFAULT_DISCARD_MESSAGE = false
        val DEFAULT_CONFINED = false
        val DEFAULT_IO_BOUND = false
        val DEFAUL_CHANNEL_SIZE = 50
    }

    open fun clear() : ActorBasicBuilder {
        actorName = null
        actorScope = DEFAULT_SCOPE
        discardMessages = DEFAULT_DISCARD_MESSAGE
        confined = DEFAULT_CONFINED
        ioBound = DEFAULT_IO_BOUND
        channelSize = DEFAUL_CHANNEL_SIZE
        actorBody = null
        suspendable = false
        parameterMap = mutableParameterMap()

        return this
    }

    fun addActorName(name : String) : ActorBasicBuilder {
        this.actorName = name.lowercase(Locale.getDefault())
        return this
    }

    internal fun setActorScope(scope : CoroutineScope): ActorBasicBuilder {
        this.actorScope = scope
        return this
    }

    @Throws(BuildException::class)
    fun addActorScope(scope : CoroutineScope) : ActorBasicBuilder {
        if(contextBuilder != null)
            throw BuildException("Unable to add scope: scope is injected by ContextBuilder")

        this.actorScope = scope
        return this
    }

    fun forcedAddActorScope(scope : CoroutineScope) : ActorBasicBuilder {
        this.actorScope = scope
        return this
    }

    fun addGlobalScope() : ActorBasicBuilder {
        this.actorScope = GlobalScope
        return this
    }

    fun addDiscardMessageOption(discardMessage : Boolean) : ActorBasicBuilder {
        this.discardMessages = discardMessages
        return this
    }

    fun addConfinedOption(confined : Boolean) : ActorBasicBuilder {
        this.confined = confined
        return this
    }

    fun addIoBoundOption(ioBound : Boolean) : ActorBasicBuilder {
        this.ioBound = ioBound
        return this
    }

    fun addChannelSizeOption(channelSize : Int) : ActorBasicBuilder {
        this.channelSize = channelSize
        return this
    }

    open fun addActorBody(actorBody : TransientActorBasicBody) : ActorBasicBuilder {
        this.actorBody = actorBody
        return this
    }

    fun addQActorBody(qActorBasic : QActorBasic, body : QActorBasic.(IApplMessage) -> Unit) :
            ActorBasicBuilder {
        return addActorBody(TransientQActorBasicBody(body, qActorBasic))
    }

    fun addQActorBody(body : QActorBasic.(IApplMessage) -> Unit) : ActorBasicBuilder {
        return addActorBody(TransientQActorBasicBody(body, QActorBasic()))
    }

    fun addQActorMethodBody(method : Method, instance : QActorBasic) : ActorBasicBuilder {
        return addActorBody(TransientQActorMethodBody(method, instance))
    }

    fun addActorBody(body : (IApplMessage) -> Unit) : ActorBasicBuilder {
        return addActorBody(TransientLambdaActorBasicBody(body))
    }

    fun addAutoActorBody(clazz : Class<out AutoQActorBasic>,
                         instance : AutoQActorBasic = clazz.getConstructor().newInstance()) : ActorBasicBuilder
    {
        return addActorBody(TransientAutoActorBasicClassBody(clazz, instance))
    }

    fun addActorBasicClassBody(clazz : Class<out ActorBasic>) : ActorBasicBuilder {
        return addActorBody(TransientActorBasicClassBody(clazz))
    }

    fun addParameter(name : String, param : Any) : ActorBasicBuilder {
        parameterMap.addParam(name, param)
        return this
    }

    fun removeParameter(name : String) : ActorBasicBuilder {
        parameterMap.removeParam(name)
        return this
    }

    fun withParameters(action : (MutableParameterMap).() -> Unit) : ActorBasicBuilder {
        action(parameterMap)
        return this
    }

    @Throws(BuildException::class)
    open fun build() : TransientActorBasic {
        if(actorName == null)
            throw BuildException("Actor name can not be null. Please call \'addActorName()\' before")
        if(actorBody == null)
            throw BuildException("Actor body can not be null. Please call \'addActorBody()\' before")

        val actor = TransientActorBasic(actorName!!, actorScope, discardMessages,
            confined, ioBound, channelSize, actorBody!!, parameterMap.asImmutable())
        clear()

        return actor
    }

    @Throws(BuildException::class)
    open fun buildInContext() : Pair<TransientActorBasic, ContextBuilder> {
        if(contextBuilder == null)
            throw BuildException("Unable to build inside context: this builder is not provided by a ${ContextBuilder::class.java.simpleName} instance")

        val actor = build()
        contextBuilder.addActor(actor)
        return Pair(actor, contextBuilder)
    }

    @Throws(BuildException::class)
    fun upgrateToFsmBuilder() : ActorBasicFsmBuilder {
        if(actorBody != null)
            throw BuildException("Unable to upgrade: an ${ActorBasic::class.java.simpleName} body is already set")

        return asFsmBuilder()
    }

    fun asFsmBuilder() : ActorBasicFsmBuilder {
        val fsmBuilder = ActorBasicFsmBuilder(contextBuilder)
        if(actorName != null) fsmBuilder.addActorName(actorName!!)
        fsmBuilder.forcedAddActorScope(actorScope).addDiscardMessageOption(discardMessages)
            .addConfinedOption(confined).addIoBoundOption(ioBound)
            .addChannelSizeOption(channelSize)
        for(param in parameterMap) {
            fsmBuilder.addParameter(param.key, param.value)
        }

        return fsmBuilder
    }

}