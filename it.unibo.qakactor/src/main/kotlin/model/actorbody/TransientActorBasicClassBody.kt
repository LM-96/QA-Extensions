package it.unibo.kactor.model.actorbody

import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class TransientActorBasicClassBody<T : ActorBasic>(
    val clazz : Class<T>
) : TransientActorBasicBody {
    //N.B: includes also ActorBasicFsm classes

    @Throws(NoSuchMethodException::class, SecurityException::class)
    fun newInstance(name : String, scope : CoroutineScope) : ActorBasic {
        return try {
            clazz.getConstructor(String::class.java, CoroutineScope::class.java).newInstance(name, scope)
        } catch (_ : Exception) {
            clazz.getConstructor(String::class.java).newInstance(name)
        }
    }

    @Throws(NoSuchMethodException::class, SecurityException::class)
    fun newInstance(name : String) : ActorBasic {
        return clazz.getConstructor(String::class.java).newInstance(name)
    }

    @Throws(NoSuchMethodException::class, SecurityException::class)
    fun newInstance(name: String, scope: CoroutineScope, discardMessages : Boolean, confined : Boolean,
                    ioBound : Boolean, channelSize : Int) : ActorBasic {
        return clazz.getConstructor(String::class.java, CoroutineScope::class.java, Boolean::class.java,
            Boolean::class.java, Int::class.java)
            .newInstance(name, scope, discardMessages,confined, ioBound, channelSize)
    }

}