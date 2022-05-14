package it.unibo.kactor.builders

import it.unibo.kactor.model.TransientContext
import it.unibo.kactor.model.TransientSystem
import it.unibo.kactor.utils.ParameterMap
import it.unibo.kactor.utils.lateSingleInit
import it.unibo.kactor.utils.mutableParameterMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

internal val sysBuilder = SystemBuilder()

class SystemBuilder {

    private var hostname : String? = null
    private val contexts = mutableSetOf<TransientContext>()
    private var scope : CoroutineScope? = null
    private val params = mutableParameterMap()

    private val contextBuilder = ContextBuilder(this)
    private val alreadyBuilt = lateSingleInit<Unit>()

    private val builtEvent = MutableStateFlow<String>("")

    companion object {
        private val BUILT_KEY = "built"
    }

    fun addHostname(hostname : String) : SystemBuilder {
        this.hostname = hostname

        return this
    }

    internal fun addContext(context: TransientContext) : SystemBuilder {
        contexts.add(context)

        return this
    }

    fun newContext() : ContextBuilder {
        contextBuilder.clear()
        if(scope != null)
            contextBuilder.addContextScope(scope!!)
        return contextBuilder
    }

    fun addParameter(name : String, obj : Any) : SystemBuilder {
        params[name] = obj
        return this
    }

    fun addScope(scope : CoroutineScope) : SystemBuilder {
        this.scope = scope
        return this
    }

    fun addGlobalScope() : SystemBuilder {
        this.scope = GlobalScope
        return this
    }

    fun removeParameter(name : String) : SystemBuilder {
        params.removeParam(name)
        return this
    }

    fun withParameterMap(action : ParameterMap.() -> Unit) : SystemBuilder{
        action.invoke(params)
        return this
    }

    suspend fun waitBuild() {
        builtEvent.first { it == BUILT_KEY }
    }

    @Throws(BuildException::class)
    suspend fun build() : TransientSystem {
        if(alreadyBuilt.isInitialized())
            throw BuildException("System has already been builded: unable to build again")
        if(hostname == null)
            throw BuildException("Host name can not be null. Please call \'addHostname()\' before")

        val built = TransientSystem(hostname!!, contexts, params.asImmutable())

        alreadyBuilt.set(Unit)
        builtEvent.emit(BUILT_KEY)

        return built
    }

}