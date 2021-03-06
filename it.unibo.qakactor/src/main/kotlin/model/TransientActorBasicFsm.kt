package it.unibo.kactor.model

import it.unibo.kactor.model.actorbody.TransientActorBasicFsmBody
import it.unibo.kactor.model.actorbody.TransientQActorBasicFsmBody
import kotlinx.coroutines.CoroutineScope

class TransientActorBasicFsm(
    actorName : String,
    actorScope : CoroutineScope,
    discardMessages : Boolean,
    confined : Boolean,
    ioBound : Boolean,
    channelSize : Int,
    actorBody : TransientActorBasicFsmBody
) : TransientActorBasic(actorName, actorScope, discardMessages, confined, ioBound, channelSize, actorBody)

fun TransientActorBasic.asFsm(fsmBody : TransientQActorBasicFsmBody) : TransientActorBasicFsm {
    return TransientActorBasicFsm(actorName, actorScope, discardMessages,
        confined, ioBound, channelSize, fsmBody)
}

fun TransientActorBasic.asFsm() : TransientActorBasicFsm {
    if(actorBody !is TransientActorBasicFsmBody)
        throw IllegalArgumentException("The behavior of this actor is not a finite state machine")

    return TransientActorBasicFsm(actorName, actorScope, discardMessages,
        confined, ioBound, channelSize, actorBody)
}