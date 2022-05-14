package it.unibo.kactor.utils

abstract class ReadableParameterMap {

    protected abstract val params : Map<String, Any>

    operator fun get(name : String) : Any? {
        return params[name]
    }

    fun checkTypeOf(name : String, clazz : Class<*>) : Boolean {
        if(params.containsKey(name))
            if(clazz.isInstance(params[name]!!))
                return true

        return false
    }

    fun checkNotTypeOf(name : String, clazz : Class<*>) : Boolean {
        if(params.containsKey(name))
            if(!clazz.isInstance(params[name]!!))
                return true

        return false
    }

    fun ifIsTypeOf(name : String, clazz : Class<*>, then : (Any) -> Unit) : ReadableParameterMap {
        if(params.containsKey(name)) {
            val param = params[name]!!
            if (clazz.isInstance(param))
                then.invoke(param)
        }

        return this
    }

    fun ifIsNotTypeOf(name : String, clazz : Class<*>, then : (Any) -> Unit) : ReadableParameterMap {
        if(params.containsKey(name)) {
            val param = params[name]!!
            if (!clazz.isInstance(param))
                then.invoke(param)
        }

        return this
    }

    fun ifNotPresent(name : String, then : () -> Unit) : ReadableParameterMap {
        if(!params.containsKey(name))
            then.invoke()

        return this
    }

    @Throws(NoSuchElementException::class)
    fun sureGet(name : String) : Any {
        if(!params.containsKey(name))
            throw NoSuchElementException("No parameter with name \'$name\'")

        return params[name]!!
    }

    @Throws(NoSuchElementException::class, ClassCastException::class)
    fun <T> sureGetAs(name : String) : T {
        if(!params.containsKey(name))
            throw NoSuchElementException("No parameter with name \'$name\'")

        try {
            return params[name] as T
        } catch (e : Exception) {
            throw ClassCastException("Unable to get the parameter with name \'$name\' as desired type")
        }
    }

    fun getParam(name : String) : Any? {
        return params[name]
    }

    fun getOrElse(name : String, elseObj : Any) : Any {
        return if(params.containsKey(name))
            params[name]!!
        else
            elseObj
    }

    inline fun <reified T> tryCastOrElse(name : String, elseObj : T) : T {
        if(this.hasParam(name)) {
            val param = this[name]
            if(param is T)
                return param
        }

        return elseObj
    }

    fun <T> castOrElse(name : String, elseObj : T) : T {
        if(params.containsKey(name))
            return params[name] as T

        return elseObj
    }

    fun hasParam(name : String) : Boolean {
        return params.containsKey(name)
    }

    fun hasParams(vararg names : String) : Boolean {
        for(name in names)
            if(!params.containsKey(name))
                return false
        return true
    }

    fun asMap() : Map<String, Any> {
        return params.toMap()
    }

    fun asMutableMap() : Map<String, Any> {
        return params.toMutableMap()
    }

    fun <T> getAs(name : String) : T? {
        if(params.containsKey(name))
            return params[name]!! as T

        return null
    }

    inline fun <reified T> tryGetAs(name : String) : T? {
        if(hasParam(name)) {
            val param = this[name]
            if(param is T)
                return param
        }

        return null
    }

    fun <T> map(name : String, mapper : (Any) -> T) : T? {
        if(params.containsKey(name))
            return mapper.invoke(params[name]!!)

        return null
    }

    fun <I,O> castAndMap(name : String, mapper : (I) -> O) : O? {
        if(params.containsKey(name))
            return mapper.invoke(params[name]!! as I)

        return null
    }

    inline fun <reified I, O> tryCastAndMap(name : String, mapper : (I) -> O) : O? {
        if(hasParam(name)) {
            val param = this[name]
            if(param is I)
                return mapper.invoke(param)
        }

        return null
    }

    inline fun <reified T> tryWithParam(name : String, action : (T) -> Unit) : ReadableParameterMap {
        if(this.hasParam(name)) {
            val param = this[name]
            if(param != null)
                if(param is T)
                    action.invoke(param)
        }

        return this
    }

    override fun toString(): String {
        return params.toString()
    }
}

class ParameterMap(initParams : Map<String, Any>? = null) : ReadableParameterMap() {

    override val params = mutableMapOf<String, Any>()

    init {
        if(initParams != null)
            params.putAll(initParams)
    }

    operator fun set(name : String, obj : Any) {
        params[name] = obj
    }

    fun addParam(name : String, value : Any) : ParameterMap {
        params[name] = value
        return this
    }

    fun addParams(vararg params : Pair<String, Any>) {
        for(p in params)
            this[p.first] = p.second
    }

    fun removeParam(name : String) : Any? {
        return params.remove(name)
    }

    fun asImmutable() : ImmutableParameterMap {
        return ImmutableParameterMap(params.toMap())
    }

    fun clear() {
        params.clear()
    }

}

class ImmutableParameterMap(
    override val params: Map<String, Any>
) : ReadableParameterMap() {

    fun mutableCopy() : ParameterMap {
        return ParameterMap(params)
    }

}

fun mutableParameterMap() : ParameterMap {
    return ParameterMap()
}

fun mutableParameterMap(initialParams : Map<String, Any>) : ParameterMap {
    return ParameterMap(initialParams)
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

fun mutableParameterMapOf(vararg params : Pair<String, Any>) : ParameterMap {
    return mutableParameterMap(params.toMap())
}

fun Map<String, Any>.toParameterMap() : ParameterMap {
    return ParameterMap(this)
}

fun Map<String, Any>.toImmutableParameterMap() : ImmutableParameterMap {
    return ImmutableParameterMap(this.toMap())
}

infix fun String.asNameOf(obj : Any) : Pair<String, Any> {
    return Pair(this, obj)
}