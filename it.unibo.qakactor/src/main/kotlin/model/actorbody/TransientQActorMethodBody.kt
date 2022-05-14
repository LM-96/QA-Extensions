package it.unibo.kactor.model.actorbody

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.IApplMessage
import it.unibo.kactor.QActorBasic
import it.unibo.kactor.utils.hasActorBasicBodySignature
import it.unibo.kactor.utils.invokeSuspend
import java.lang.reflect.Method

class TransientQActorMethodBody<T : QActorBasic>(
    val method : Method,
    val qActor : T
) : TransientQActorBasicBody<T>(getBodyFromMethod(method), qActor) {

    companion object {
        fun getBodyFromMethod(method: Method) : suspend QActorBasic.(IApplMessage) -> Unit {
            return { msg -> method.invokeSuspend(this, msg) }
        }
    }

    init {
        if(!method.hasActorBasicBodySignature())
            throw IllegalArgumentException("Method does not have the actor basic body signature")
    }

}