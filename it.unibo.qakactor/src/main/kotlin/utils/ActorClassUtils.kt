package it.unibo.kactor.utils

import it.unibo.kactor.*
import java.lang.reflect.Field

/* ACTORBASIC - ACTORBASICFSM ************************************************* */
fun ActorBasic.isActorBasicFsm(clazz : Class<*>) : Boolean {
    return hasSuperclass(clazz, ActorBasicFsm::class.java)
}

fun Class<*>.isActorBasic() : Boolean {
    return hasSuperclass(this, ActorBasic::class.java)
}

fun Class<*>.isActorBasicFsm() : Boolean {
    return hasSuperclass(this, ActorBasicFsm::class.java)
}

fun Class<*>.isOnlyActorBasic() : Boolean {
    return hasSuperclass(this, ActorBasic::class.java) &&
            !hasSuperclass(this, ActorBasicFsm::class.java)
}


/* QACTORBASIC - QACTORBASICFSM ************************************************* */
fun IQActorBasic.isQActorBasicFsm(clazz : Class<*>) : Boolean {
    return hasSuperclass(clazz, IQActorBasicFsm::class.java)
}

fun Class<*>.isQActorBasic() : Boolean {
    return hasSuperclass(this, QActorBasic::class.java)
}

fun Class<*>.isIQActorBasic() : Boolean {
    return implements(this, IQActorBasic::class.java)
}

fun Class<*>.isQActorBasicFsm() : Boolean {
    return hasSuperclass(this, QActorBasicFsm::class.java)
}

fun Class<*>.isIQActorBasicFsm() : Boolean {
    return implements(this, IQActorBasicFsm::class.java)
}

fun Class<*>.isOnlyQActorBasic() : Boolean {
    return hasSuperclass(this, QActorBasic::class.java) &&
            !hasSuperclass(this, QActorBasicFsm::class.java)
}

fun Class<*>.isOnlyIQActorBasic() : Boolean {
    return implements(this, IQActorBasic::class.java) &&
            !implements(this, IQActorBasicFsm::class.java)
}

/* AUTOQACTORBASIC - AUTOQACTORBASICFSM ************************************************* */
fun IQActorBasic.isAutoQActorBasicFsm(clazz : Class<*>) : Boolean {
    return hasSuperclass(clazz, AutoQActorBasicFsm::class.java)
}

fun Class<*>.isAutoQActorBasic() : Boolean {
    return hasSuperclass(this, AutoQActorBasic::class.java)
}

fun Class<*>.isAutoQActorBasicFsm() : Boolean {
    return hasSuperclass(this, AutoQActorBasicFsm::class.java)
}

fun Class<*>.isOnlyAutoQActorBasic() : Boolean {
    return hasSuperclass(this, AutoQActorBasic::class.java) &&
            !hasSuperclass(this, AutoQActorBasicFsm::class.java)
}

/* ACTOR CLASS TYPE *********************************************************** */
enum class ActorClassType {
    ACTOR_BASIC_ONLY, ACTOR_BASIC_FSM, QACTOR_BASIC_ONLY, QACTOR_BASIC_FSM,
    AUTO_QACTOR_BASIC_ONLY, AUTO_QACTOR_BASIC_FMS, IQACTOR_BASIC_ONLY, IQACTOR_BASIC_FSM
}

fun getActorClassType(clazz: Class<*>) : ActorClassType? {
    if(clazz.isActorBasicFsm())
        return ActorClassType.ACTOR_BASIC_FSM
    else if(clazz.isActorBasic())
        return ActorClassType.ACTOR_BASIC_ONLY
    else if(clazz.isAutoQActorBasicFsm())
        return ActorClassType.AUTO_QACTOR_BASIC_FMS
    else if (clazz.isQActorBasicFsm())
        return ActorClassType.QACTOR_BASIC_FSM
    else if(clazz.isAutoQActorBasic())
        return ActorClassType.AUTO_QACTOR_BASIC_ONLY
    else if(clazz.isQActorBasic())
        return ActorClassType.QACTOR_BASIC_ONLY
    else if(clazz.isIQActorBasicFsm())
        return ActorClassType.IQACTOR_BASIC_FSM
    else if(clazz.isIQActorBasic())
        return ActorClassType.IQACTOR_BASIC_ONLY

    return null
}

@JvmName("getMyActorClassType")
fun Class<*>.getActorClassType() : ActorClassType? {
    return getActorClassType(this)
}

fun hasSuperclass(clazz : Class<*>, superclazz : Class<*>) : Boolean {
    var sc = clazz.superclass
    while (sc != null) {
        if(sc == superclazz ) return true
        sc = sc.superclass
    }

    return false
}

fun hasSuperInterface(interfac3: Class<*>, superinterfac3 : Class<*>) : Boolean {
    if(!interfac3.isInterface)
        throw IllegalArgumentException("$interfac3 is not an interface")
    if(!superinterfac3.isInterface)
        throw IllegalArgumentException("$superinterfac3 is not an interface")

    val si = interfac3.interfaces
    for(i in si) {
        if(i == superinterfac3 || hasSuperInterface(i, superinterfac3))
            return true
    }

    return false
}

fun implements(clazz : Class<*>, interfac3 : Class<*>) : Boolean {
    if(clazz.isInterface)
        throw IllegalArgumentException("$clazz is not a class")

    if(!interfac3.isInterface)
        throw IllegalArgumentException("$interfac3 is not an interface")

    var c : Class<*>? = clazz
    while(c != null) {
        for(i in c.interfaces) {
            println("$clazz : $i")
            if(i == interfac3 || hasSuperInterface(i, interfac3))
                return true
        }
        c = c.superclass
    }

    return false
}

val Class<*>.delegates : Set<Field>
get() {
    return declaredFields.filter { it.name.contains("\$\$delegate_") }.toSet()
}

fun Class<*>.getDelegateFieldsWithType(type : Class<*>) : Set<Field> {
    return declaredFields.filter { it.name.contains("\$\$delegate_") &&
            it.type == type }.toSet()
}

fun <T, R> Class<T>.getDelegateObjectsWithType(instance : T, type : Class<R>) : Set<R> {
    return declaredFields.filter { it.name.contains("\$\$delegate_") &&
            it.type == type }.map { it.get(instance) as R }.toSet()
}