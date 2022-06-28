package it.unibo.kactor.builders

import it.unibo.kactor.annotations.QakContext
import it.unibo.kactor.model.TransientActorBasic
import it.unibo.kactor.model.TransientContext
import it.unibo.kactor.parameters.MutableParameterMap
import it.unibo.kactor.parameters.mutableParameterMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class ContextBuilder(private val systemBuilder: SystemBuilder? = null) {

    private var contextName : String? = null
    private var contextAddress : String? = null
    private var contextProtocol : String = "TCP"
    private var contextPort : Int? = null
    private var contextScope : CoroutineScope? = null

    private var actors : MutableMap<String, TransientActorBasic>? = null

    private val actorBuilder = ActorBasicBuilder(this)
    private val actorFsmBuilder = ActorBasicFsmBuilder(this)

    private var params = mutableParameterMap()

    fun clear() {
        contextName = null
        contextAddress = null
        contextProtocol = "TCO"
        contextPort = null
        contextScope = null

        actors = null
        params = mutableParameterMap()
        actorBuilder.clear()
        actorFsmBuilder.clear()
    }

    fun addContextName(ctxName : String) : ContextBuilder {
        this.contextName = ctxName
        return this
    }

    fun addContextAddress(ctxAddr : String) : ContextBuilder {
        this.contextAddress = ctxAddr
        return this
    }

    fun addContextProtocol(ctxProtocol : String) : ContextBuilder {
        this.contextProtocol = ctxProtocol
        return this
    }

    fun addContextPort(ctxPort : Int) : ContextBuilder {
        this.contextPort = ctxPort
        return this
    }

    fun addContextScope(scope : CoroutineScope) : ContextBuilder {
        this.contextScope = scope
        return this
    }

    fun addGlobalScope() : ContextBuilder {
        this.contextScope = GlobalScope
        return this
    }

    fun addByContextAnnotation(ann : QakContext) : ContextBuilder {
        contextName = ann.contextName
        contextPort = ann.contextPort
        contextAddress = ann.contextAddress
        contextProtocol = ann.contextProtocol

        return this
    }

    fun addParameter(name : String, value : Any) : ContextBuilder {
        params.addParam(name, value)
        return this
    }

    fun removeParameter(name : String) : ContextBuilder {
        params.removeParam(name)
        return this
    }

    fun withParameterMap(action : MutableParameterMap.() -> Unit) : ContextBuilder {
        action.invoke(params)
        return this
    }

    @Throws(BuildException::class)
    fun newActorBasic() : ActorBasicBuilder {
        if(actors == null)
            actors = mutableMapOf()

        if(contextScope == null)
            throw BuildException("Context scope can not be null while using this method")

        actorBuilder.clear()
        actorBuilder.setActorScope(contextScope!!)
        return actorBuilder
    }

    @Throws(BuildException::class)
    fun newActorBasicFsm() : ActorBasicFsmBuilder {
        if(actors == null)
            actors = mutableMapOf()

        if(contextScope == null)
            throw BuildException("Context scope can not be null while using this method")

        actorBuilder.clear()
        actorBuilder.setActorScope(contextScope!!)
        return actorFsmBuilder
    }

    @Throws(BuildException::class)
    fun build() : TransientContext {
        if(contextName == null)
            throw BuildException("Context name can not be null")
        if(contextAddress == null)
            throw BuildException("Context address can not be null")
        if(contextPort == null)
            throw BuildException("Context port can not be null")
        if(contextScope == null)
            throw BuildException("Context scope can not be null")
        if(actors == null)
            actors = mutableMapOf()


        val ctx : TransientContext = TransientContext(contextName!!, contextAddress!!, contextPort!!,
            contextProtocol!!, actors!!.values.toSet(), contextScope!!)

        clear()

        return ctx
    }

    fun buildInSystem() : Pair<TransientContext, SystemBuilder> {
        if(systemBuilder == null)
            throw BuildException("Unable to build as system context: this builder is not provided by a ${SystemBuilder::class.java.simpleName} instance")

        val ctx = build()
        systemBuilder.addContext(ctx)
        return Pair(ctx, systemBuilder)
    }

    @Throws(BuildException::class)
    internal fun addActor(actor : TransientActorBasic) {
        if(actors!!.containsKey(actor.actorName))
            throw BuildException("Unable to have to actor with the same name (\"${actor.actorName}\"")

        actors!![actor.actorName] = actor
    }

}