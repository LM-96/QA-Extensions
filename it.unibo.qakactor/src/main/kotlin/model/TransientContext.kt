package it.unibo.kactor.model

import it.unibo.kactor.annotations.QakContext
import it.unibo.kactor.parameters.ImmutableParameterMap
import it.unibo.kactor.parameters.immutableParameterMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

data class TransientContext(
    val contextName : String,
    val contextAddress : String,
    val contextPort : Int,
    val contextProtocol : String = "TCP",
    val actors : Set<TransientActorBasic> = setOf(),
    val scope : CoroutineScope = GlobalScope,
    val params: ImmutableParameterMap = immutableParameterMap()
) {

    fun ctxEquals(ctx : it.unibo.kactor.QakContext) : Boolean {
        return ctx.name == contextName &&
                ctx.hostAddr == contextAddress &&
                ctx.portNum == contextPort
    }

    fun getTransientActorBasic(name : String) : TransientActorBasic? {
        return actors.find { it.actorName == name }
    }

    @Throws(IllegalArgumentException::class)
    fun getTransientActorBasicFsm(name : String) : TransientActorBasicFsm? {
        val actor = actors.find { it.actorName == name }
        if(actor != null)
            if(actor is TransientActorBasicFsm)
                return actor
        return null
    }
}

fun QakContext.toTransientContext() : TransientContext {
    return TransientContext(contextName, contextAddress, contextPort, contextProtocol)
}

fun QakContext.toTransientContext(actors : Set<TransientActorBasic> = setOf(), scope : CoroutineScope = GlobalScope) : TransientContext {
    return TransientContext(contextName, contextAddress, contextPort, contextProtocol, actors, scope)
}