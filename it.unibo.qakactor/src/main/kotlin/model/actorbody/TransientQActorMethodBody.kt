package it.unibo.kactor.model.actorbody

import it.unibo.kactor.IApplMessage
import it.unibo.kactor.IQActorBasic
import it.unibo.kactor.utils.hasActorBasicBodySignature
import it.unibo.kactor.utils.invokeSuspend
import java.lang.reflect.Method

class TransientQActorMethodBody<T : IQActorBasic>(
    val method : Method,
    val qActor : T
) : TransientQActorBasicBody<T>(getBodyFromMethod(method), qActor) {

    companion object {
        fun getBodyFromMethod(method: Method) : suspend IQActorBasic.(IApplMessage) -> Unit {
            return { msg -> method.invokeSuspend(this.instance.get(), msg) }
        }
    }

    init {
        if(!method.hasActorBasicBodySignature())
            throw IllegalArgumentException("Method does not have the actor basic body signature")
    }

}