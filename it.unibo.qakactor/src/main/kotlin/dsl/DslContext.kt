package it.unibo.kactor.dsl

import it.unibo.kactor.builders.ActorBasicFsmBuilder
import it.unibo.kactor.builders.ContextBuilder
import it.unibo.kactor.builders.sysBuilder

enum class Protocol{
    TCP
}
val TCP = Protocol.TCP

/* CONTEXT DEFINITION */
@QActorDsl object context

@QActorDsl data class ContextNameContinuation(val name : String)
@QActorDsl data class ContextAddressContinuation(val name : String, val host : String)
@QActorDsl data class ContextPortContinuation(val name : String, val host : String, val port : Int)
@QActorDsl data class ContextBuildContinuation(val name : String, val host : String, val port : Int,
                                               val protocol : Protocol?, val contextBuilder : ContextBuilder)

@QActorDsl
infix fun context.name(name : String)  : ContextNameContinuation {
    return ContextNameContinuation(name)
}

@QActorDsl
infix fun ContextNameContinuation.host(hostName : String) : ContextAddressContinuation {
    return ContextAddressContinuation(name, hostName)
}

@QActorDsl
infix fun ContextAddressContinuation.port(port : Int) : ContextPortContinuation {
    return ContextPortContinuation(name, host, port)
}

@QActorDsl
infix fun ContextPortContinuation.withActors(generator: DslContext.() -> Unit) {
    this.protocol(null).withActors(generator)
}

@QActorDsl
infix fun ContextPortContinuation.remoteWithActors(generator: RemoteDslContext.() -> Unit) {
    this.protocol(null).remoteWithActors(generator)
}

@QActorDsl
infix fun ContextPortContinuation.protocol(protocol: Protocol?) : ContextBuildContinuation {
    println("          %%% dsl | Adding context [name=$name, host=$host, port=$port, protocol=$protocol]")
    val ctxBuilder = sysBuilder.newContext()
        .addContextName(name)
        .addContextAddress(host)
        .addContextPort(port)
    if(protocol != null) ctxBuilder.addContextProtocol(protocol.toString())
    else ctxBuilder.addContextProtocol("TCP")

    return ContextBuildContinuation(name, host, port, protocol, ctxBuilder)
}

@QActorDsl
infix fun ContextBuildContinuation.withActors(generator : DslContext.() -> Unit) {
    generator.invoke(DslContext(name, contextBuilder))
    contextBuilder.buildInSystem()
    println("          %%% dsl | Context \'$name\' built")
}

infix fun ContextBuildContinuation.remoteWithActors(generator : RemoteDslContext.() -> Unit) {
    //contextBuilder.markAsRemote()
    generator.invoke(RemoteDslContext(name, contextBuilder))
    contextBuilder.buildInSystem()
    println("          %%% dsl | Remote Context \'$name\' built")
}

@QActorDsl object Qactor

@QActorDsl data class ActorNameContinuation(val name : String, val actorBuilder : ActorBasicFsmBuilder)
@QActorDsl data class StateNameContinuation(val name : String)

@QActorDsl
class DslContext(
    val contextName : String,
    private val contextBuilder: ContextBuilder) {

    @QActorDsl
    infix fun Qactor.name(name : String) : ActorNameContinuation {
        println("          %%% dsl | Adding actor [name=$name]")
        val actorBuilder = this@DslContext.contextBuilder.newActorBasicFsm()
        actorBuilder.addActorName(name)
        return ActorNameContinuation(name, actorBuilder)
    }

    @QActorDsl
    infix fun ActorNameContinuation.definedBy(generator : DslQActor.() -> Unit) {
        val dslQActor = DslQActor(actorBuilder)
        generator.invoke(dslQActor)
        try {dslQActor.stateBuilder.buildState()} catch (_: Exception){}
        actorBuilder.buildInContext()
        println("          %%% dsl | Actor [name=$name] built")
    }

}

@QActorDsl
class RemoteDslContext(
    val contextName : String,
    private val contextBuilder: ContextBuilder) {

    @QActorDsl
    infix fun Qactor.name(name : String) {
        println("          %%% dsl | Adding remote actor [name=$name]")
        //this@RemoteDslContext.contextBuilder.addRemoteActor(name)
    }

}