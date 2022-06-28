package it.unibo.kactor.model

import it.unibo.kactor.model.actorbody.TransientActorBasicBody
import it.unibo.kactor.parameters.ImmutableParameterMap
import kotlinx.coroutines.CoroutineScope

open class TransientActorBasic(
    val actorName : String,
    val actorScope : CoroutineScope,
    val discardMessages : Boolean,
    val confined : Boolean,
    val ioBound : Boolean,
    val channelSize : Int,
    val actorBody : TransientActorBasicBody/*? = null*/,
    val parameters : ImmutableParameterMap
) {

    override fun toString(): String {
        return "TransientActorBasic(actorName='$actorName', actorScope=$actorScope, discardMessages=$discardMessages, confined=$confined, ioBound=$ioBound, channelSize=$channelSize, actorBody=$actorBody)"
    }
}