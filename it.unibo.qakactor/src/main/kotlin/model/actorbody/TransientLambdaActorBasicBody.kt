package it.unibo.kactor.model.actorbody

import it.unibo.kactor.IApplMessage
import it.unibo.kactor.model.actorbody.TransientActorBasicBody

open class TransientLambdaActorBasicBody(
    val action : suspend (IApplMessage) -> Unit
) : TransientActorBasicBody
