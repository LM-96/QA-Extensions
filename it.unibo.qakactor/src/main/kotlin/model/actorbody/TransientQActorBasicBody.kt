package it.unibo.kactor.model.actorbody

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.IApplMessage
import it.unibo.kactor.QActorBasic

open class TransientQActorBasicBody<T : QActorBasic> internal constructor(
    qActorBody : suspend QActorBasic.(IApplMessage) -> Unit,
    val qActorBasic: T,
) : TransientLambdaActorBasicBody( { msg -> qActorBody.invoke(qActorBasic, msg)} ), TransientActorBasicBody {

    @Throws(Exception::class)
    fun injectActorBasic(actorBasic : ActorBasic) {
        val field = QActorBasic::class.java.getDeclaredField("actor")
        field.trySetAccessible()
        field.set(qActorBasic, actorBasic)
    }

}