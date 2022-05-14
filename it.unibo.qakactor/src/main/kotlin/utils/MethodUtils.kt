package it.unibo.kactor.utils

import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.IApplMessage
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf

fun Method.countParameters() : Int {
    val kMethod = kotlinFunction
    return kMethod?.valueParameters?.size ?: parameterCount
}

fun Method.hasParameters() : Boolean {
    val kMethod = kotlinFunction
    return kMethod?.valueParameters?.isNotEmpty() ?: parameters.isNotEmpty()
}

fun Method.getKParameters() : List<KParameter>? {
    val kMethod = kotlinFunction
    return kMethod?.valueParameters
}

fun Method.checkType(param : Int, clazz : KClass<*>) : Boolean {
    val kParam = kotlinFunction?.valueParameters
    return if(kParam != null)
        kParam[param].type.jvmErasure == clazz
    else
        parameterTypes[param] == clazz.java
}

/*
suspend fun Method.invokeSuspendWithNoParam(obj : Any) {
   val kMethod = kotlinFunction
    if(kMethod != null) {
        if (kMethod.isSuspend)
            kMethod.callSuspend(obj)
        else
            kMethod.call(obj)
    } else invoke(obj)
}*/

/*suspend fun Method.invokeSuspend(obj : Any, param : Any) {
    val kMethod = kotlinFunction
    if(kMethod != null) {
        if (kMethod.isSuspend)
            kMethod.callSuspend(obj, param)
        else
            kMethod.call(obj)
    } else invoke(obj)
}*/

suspend fun Method.invokeSuspend(obj : Any, vararg param : Any?) : Any =
    suspendCoroutineUninterceptedOrReturn { continuation -> invoke(obj, *param, continuation) }

//Kotlin signature: suspend fun actorBody(msg : IApplMessage)
//Java   signature: public Object actorBody(@NotNull IApplMessage msg, @NotNull Continuation<? super Unit> $completion)
fun Method.isAutoQActorBasicBody() : Boolean {
    if(this.declaringClass.isOnlyAutoQActorBasic()) {
        if(this.name == "actorBody") {
            /*val kFun = this.kotlinFunction
            if(kFun != null) { //Is Kotlin function
                val params = kFun.valueParameters
                if(params.size == 1 && kFun.returnType == typeOf<Unit>()) {
                    if(params[0].type == typeOf<IApplMessage>())
                        return true
                }
            } else { //Is Java function
                val params = this.parameterTypes
                if(params.size == 2 && this.returnType == Object::class.java)
                    if(params[0] == IApplMessage::class.java && params[1] == Continuation::class.java)
                        return true
            }*/
            return hasActorBasicBodySignature(true)
        }
    }

    return false
}


fun Method.hasActorBasicBodySignature(mustBeSuspend : Boolean = false) : Boolean {
    val kFun = this.kotlinFunction
    if(kFun != null) { //Is Kotlin function
        val params = kFun.valueParameters
        if(params.size == 1 && kFun.returnType == typeOf<Unit>()) {
            if(params[0].type == typeOf<IApplMessage>())
                return true
        }
    } else { //Is Java function
        if( this.returnType == Object::class.java ) {
            val params = this.parameterTypes
            if(mustBeSuspend) {
                if (params.size == 2)
                    if (params[0] == IApplMessage::class.java && params[1] == Continuation::class.java)
                        return true
            } else {
                if (params.size == 1)
                    if(params[0] == IApplMessage::class.java)
                        return true
            }
        }
    }

    return false
}

//Kotlin signature: fun getBody(): (ActorBasicFsm.() -> Unit)
//Java   signature: public Function1<ActorBasicFsm, Unit> getBody()
fun Method.isAutoQActorBasicFsmBody() : Boolean {
    if(this.declaringClass.isQActorBasicFsm()) {
        if(this.name == "getBody") {
            /*val kFun = this.kotlinFunction
            if(kFun != null) { //Is Kotlin function
                val params = kFun.valueParameters
                if(params.isEmpty()) {
                    if(kFun.returnType == typeOf<ActorBasicFsm.() -> Unit>())
                        return true
                }
            } else { //Is Java function
                val params = this.parameterTypes
                if(params.isEmpty())
                    if(this.returnType == Function1::class.java)
                        return true
            }*/
            return hasActorBasicFsmGetBodySignature()
        }
    }

    return false
}

fun Method.hasActorBasicFsmGetBodySignature() : Boolean {
    val kFun = this.kotlinFunction
    if(kFun != null) { //Is Kotlin function
        val params = kFun.valueParameters
        if(params.isEmpty()) {
            if(kFun.returnType == typeOf<ActorBasicFsm.() -> Unit>())
                return true
        }
    } else { //Is Java function
        val params = this.parameterTypes
        if(params.isEmpty())
            if(this.returnType == Function1::class.java)
                return true
    }

    return false
}

//Kotlin signature: fun getInitialState(): String
//Java   signature: public String getInitialState()
fun Method.isGetInitialState() : Boolean {
    if(this.declaringClass.isQActorBasicFsm()) {
        if(this.name == "getInitialState") {
            /*val kFun = this.kotlinFunction
            if(kFun != null) { //Is Kotlin function
                val params = kFun.valueParameters
                if(params.isEmpty()) {
                    if(kFun.returnType == typeOf<String>())
                        return true
                }
            } else { //Is Java function
                val params = this.parameterTypes
                if(params.isEmpty())
                    if(this.returnType == String::class.java)
                        return true
            }*/
            return hasActorBasicFsmGetInitialStateSignature()
        }
    }

    return false
}

fun Method.hasActorBasicFsmGetInitialStateSignature() : Boolean {
    val kFun = this.kotlinFunction
    if(kFun != null) { //Is Kotlin function
        val params = kFun.valueParameters
        if(params.isEmpty()) {
            if(kFun.returnType == typeOf<String>())
                return true
        }
    } else { //Is Java function
        val params = this.parameterTypes
        if(params.isEmpty())
            if(this.returnType == String::class.java)
                return true
    }

    return false
}

fun Method.checkSignature(name : String, returnType : Class<*>, vararg paramTypes : Class<*>) : Boolean {
    if(this.name == name) {
        val kotlinFunction = this.kotlinFunction
        if(kotlinFunction != null) { //Kotlin Function
            if(kotlinFunction.returnType.jvmErasure.java == returnType) {
                val params = kotlinFunction.valueParameters.map { it.type.jvmErasure.java }
                if(params.size == paramTypes.size) {
                    for (p in paramTypes)
                        if (!params.contains(p))
                            return false
                    return true
                }
            }

        } else { //Java Method
            if(this.returnType == returnType) {
                val params = this.parameterTypes
                if(params.size == paramTypes.size) {
                    for(p in paramTypes)
                        if(!params.contains(p))
                            return false
                    return true
                }
            }
        }
    }

    return false
}