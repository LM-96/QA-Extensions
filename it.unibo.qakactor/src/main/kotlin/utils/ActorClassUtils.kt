package it.unibo.kactor.utils

import it.unibo.kactor.*

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
fun QActorBasic.isQActorBasicFsm(clazz : Class<*>) : Boolean {
    return hasSuperclass(clazz, QActorBasicFsm::class.java)
}

fun Class<*>.isQActorBasic() : Boolean {
    return hasSuperclass(this, QActorBasic::class.java)
}

fun Class<*>.isQActorBasicFsm() : Boolean {
    return hasSuperclass(this, QActorBasicFsm::class.java)
}

fun Class<*>.isOnlyQActorBasic() : Boolean {
    return hasSuperclass(this, QActorBasic::class.java) &&
            !hasSuperclass(this, QActorBasicFsm::class.java)
}

/* AUTOQACTORBASIC - AUTOQACTORBASICFSM ************************************************* */
fun QActorBasic.isAutoQActorBasicFsm(clazz : Class<*>) : Boolean {
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
    AUTO_QACTOR_BASIC_ONLY, AUTO_QACTOR_BASIC_FMS
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