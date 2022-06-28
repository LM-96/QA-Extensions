package it.unibo.kactor.model.actorbody

import it.unibo.kactor.*
import it.unibo.kactor.utils.getDelegateFieldsWithType
import java.lang.reflect.Field

open class TransientQActorBasicBody<T : IQActorBasic> internal constructor(
    qActorBody : suspend IQActorBasic.(IApplMessage) -> Unit,
    val qActorBasic: T,
) : TransientLambdaActorBasicBody( { msg -> qActorBody.invoke(qActorBasic, msg)} ), TransientActorBasicBody {

    @Throws(Exception::class)
    fun injectActorBasic(actorBasic : ActorBasic) {
        var field : Field? = null
        if(QActorBasic::class.java.isInstance(qActorBasic)) {
            field = IQActorBasic::class.java.getDeclaredField("actor")
        } else{
            try {
                val delqABFsm = qActorBasic.javaClass.getDelegateFieldsWithType(QActorBasic::class.java).first()
                QActorBasic::class.java.getDeclaredField("actor")
            } catch (e : Exception) {
                throw IllegalStateException("injection is not supported for class ${qActorBasic::class.java}")
            }

        }

        field!!.isAccessible = true
        field.set(qActorBasic, actorBasic)
    }

}