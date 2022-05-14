package it.unibo.kactor.model.actorbody

import it.unibo.kactor.AutoQActorBasic
import it.unibo.kactor.IApplMessage
import it.unibo.kactor.QActorBasic
import it.unibo.kactor.utils.invokeSuspend
import it.unibo.kactor.utils.isAutoQActorBasicBody

class TransientAutoActorBasicClassBody(
    val clazz : Class<out AutoQActorBasic>,
    instance: AutoQActorBasic = clazz.getConstructor().newInstance()
) : TransientQActorBasicBody<AutoQActorBasic>(asQActorBasicBody(clazz), instance){

    companion object {
        private fun asQActorBasicBody(clazz : Class<out AutoQActorBasic>) : suspend QActorBasic.(IApplMessage) -> Unit{
            val getBodyMethod = clazz.declaredMethods.find { it.isAutoQActorBasicBody() }
                ?: throw IllegalArgumentException("The class \'$clazz\' does not declare the \'actorBody()\' method")

            return { msg : IApplMessage -> getBodyMethod.invokeSuspend(this, msg) }
        }
    }

}