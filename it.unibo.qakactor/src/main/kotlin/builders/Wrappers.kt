package it.unibo.kactor.builders

import it.unibo.kactor.*
import it.unibo.kactor.model.TransientActorBasic
import it.unibo.kactor.model.TransientActorBasicFsm
import it.unibo.kactor.model.actorbody.*
import it.unibo.kactor.model.asFsm
import it.unibo.kactor.parameters.ReadableOnlyParametersOwner

/* ACTORBASIC WRAPPER *************************************************************************************** */
internal class ActorBasicWrapper(tActorBasic : TransientActorBasic) : ActorBasic(
    tActorBasic.actorName, tActorBasic.actorScope, tActorBasic.discardMessages, tActorBasic.confined,
    tActorBasic.ioBound, tActorBasic.channelSize), ReadableOnlyParametersOwner {

    private val tBody : suspend (IApplMessage) -> Unit
    override val readOnlyParameters = tActorBasic.parameters

    init {
        tBody = when (tActorBasic.actorBody) {
            is TransientQActorBasicBody<out QActorBasic> -> {
                tActorBasic.actorBody.injectActorBasic(this)
                tActorBasic.actorBody.action
            }

            is TransientLambdaActorBasicBody -> {
                tActorBasic.actorBody.action
            }

            else -> throw IllegalArgumentException("Unsupported body type: \'${tActorBasic.actorBody::class.java.simpleName}\'")
        }
    }

    override suspend fun actorBody(msg: IApplMessage) {
        tBody.invoke(msg)
    }
}

/*  FSM WRAPPER ********************************************************************************************* */
internal class ActorBasicFsmWrapper(tActorBasicFsm: TransientActorBasicFsm) :
    ActorBasicFsm(tActorBasicFsm.actorName, tActorBasicFsm.actorScope, tActorBasicFsm.discardMessages,
        tActorBasicFsm.confined, tActorBasicFsm.ioBound, tActorBasicFsm.channelSize, false, false),
        ReadableOnlyParametersOwner
{

    private val tBody : TransientActorBasicFsmBody
    override val readOnlyParameters = tActorBasicFsm.parameters

    init {

        tBody = when(tActorBasicFsm.actorBody) {
            is TransientQActorBasicFsmBody -> {
                tActorBasicFsm.actorBody.injectActorBasicFsm(this)
                tActorBasicFsm.actorBody
            }

            is TransientAutoActorBasicFsmClassBody -> {
                tActorBasicFsm.actorBody.injectActorBasicFsm(this)
                tActorBasicFsm.actorBody
            }

            else -> throw IllegalArgumentException("Unsupported body type: \'${tActorBasicFsm.actorBody::class.java.simpleName}\'")
        }

        setBody(getBody(), getInitialState(), false)
    }

    override fun getBody(): ActorBasicFsm.() -> Unit {
        return tBody.body
    }

    override fun getInitialState(): String {
        return tBody.initialState
    }
}


/* WRAPPER OBJECT ******************************************************************************************* */
object TransientWrapper {
    @Throws(WrapException::class)
    fun wrap(transientActor: TransientActorBasic) : ActorBasic {
        try {
            return when(transientActor.actorBody) {
                is TransientActorBasicFsmBody -> {
                    ActorBasicFsmWrapper(transientActor.asFsm())
                }

                is TransientActorBasicClassBody<*> -> {
                    //If is an ActorBasic instance, then no need wrappers
                    //N.B: includes also ActorBasicFsm class
                    transientActor.actorBody.newInstance(transientActor.actorName, transientActor.actorScope)
                }

                else -> {
                    ActorBasicWrapper(transientActor)
                }
            }
        } catch (e : Exception) {
            throw WrapException("Unable to wrap transient actor \'${transientActor.actorName}\'", e)
        }
    }
}

@Throws(WrapException::class)
fun TransientActorBasic.wrap() : ActorBasic {
    return TransientWrapper.wrap(this)
}

@Throws(WrapException::class)
fun wrapped(transientActor : TransientActorBasic) : ActorBasic {
    return TransientWrapper.wrap(transientActor)
}

@Throws(WrapException::class)
fun wrapAndThen(transientActor: TransientActorBasic, action : ActorBasic.() -> Unit) {
    val actorBasic = TransientWrapper.wrap(transientActor)
    action.invoke(actorBasic)
}