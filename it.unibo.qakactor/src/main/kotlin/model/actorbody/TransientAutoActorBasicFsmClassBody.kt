package it.unibo.kactor.model.actorbody

import it.unibo.kactor.*
import it.unibo.kactor.utils.isAutoQActorBasicFsmBody
import it.unibo.kactor.utils.isGetInitialState

class TransientAutoActorBasicFsmClassBody(
    val clazz : Class<out AutoQActorBasicFsm>,
    val qActorBasicFsm: AutoQActorBasicFsm = clazz.getConstructor().newInstance()
) : TransientActorBasicFsmBody(getBodyFromAutoClass(clazz, qActorBasicFsm), getInitialStateFromAutoClass(clazz, qActorBasicFsm)){

    companion object {
        private fun getBodyFromAutoClass(clazz : Class<out AutoQActorBasicFsm>, instance : AutoQActorBasicFsm) : ActorBasicFsm.() -> Unit {
            val getBodyMethod = clazz.declaredMethods.find { it.isAutoQActorBasicFsmBody() }
                ?: throw IllegalArgumentException("The class \'$clazz\' does not declare the \'getActorBody()\' method")

            return getBodyMethod.invoke(instance) as ActorBasicFsm.() -> Unit
        }

        private fun getInitialStateFromAutoClass(clazz : Class<out AutoQActorBasicFsm>, instance : AutoQActorBasicFsm) : String {
            val getInitialStateMethod = clazz.declaredMethods.find { it.isGetInitialState() }
                ?: throw IllegalArgumentException("The class \'$clazz\' does not declare the \'getInitialState()\' method")

            return getInitialStateMethod.invoke(instance) as String
        }
    }

    fun injectActorBasicFsm(actorBasicFsm: ActorBasicFsm) {
        val field = QActorBasic::class.java.getDeclaredField("actor")
        field.trySetAccessible()
        field.set(qActorBasicFsm, actorBasicFsm)
    }

}