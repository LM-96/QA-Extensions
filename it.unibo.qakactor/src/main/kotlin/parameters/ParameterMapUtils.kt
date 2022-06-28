package it.unibo.kactor.parameters

import org.apache.commons.lang3.mutable.Mutable

fun mutableParameterMap() : MutableParameterMap {
    return MutableParameterMap()
}

fun mutableParameterMap(initialParams : Map<String, Any>) : MutableParameterMap {
    return MutableParameterMap(initialParams)
}

fun immutableParameterMap(params : Map<String, Any>) : ImmutableParameterMap {
    return ImmutableParameterMap(params)
}

fun immutableParameterMap() : ImmutableParameterMap {
    return ImmutableParameterMap(mapOf())
}

fun immutableParameterMapOf(vararg params : Pair<String, Any>) : ImmutableParameterMap {
    return immutableParameterMap(params.toMap())
}

fun mutableParameterMapOf(vararg params : Pair<String, Any>) : MutableParameterMap {
    return mutableParameterMap(params.toMap())
}

fun Map<String, Any>.toParameterMap() : MutableParameterMap {
    return MutableParameterMap(this)
}

fun Map<String, Any>.toImmutableParameterMap() : ImmutableParameterMap {
    return ImmutableParameterMap(this.toMap())
}

infix fun String.asNameOf(obj : Any) : Pair<String, Any> {
    return Pair(this, obj)
}