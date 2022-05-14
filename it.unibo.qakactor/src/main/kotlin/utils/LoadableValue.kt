package it.unibo.kactor.utils

enum class ValueStatus {
    UNTOUCHED, LOADED, INJECTED, UNLOADED, NOT_FOUND
}

class LoadResult<T> internal constructor(
    loadedValue : T?,
    status : ValueStatus
) : LoadableValue<T>() {

    init {
        this.value = loadedValue
        this.status = status
    }

    fun newMutable() : MutableLoadedValue<T> {
        return when(status) {
            ValueStatus.UNLOADED -> mutableUnloadedValue()
            ValueStatus.UNTOUCHED -> mutableLoadableValue()
            ValueStatus.INJECTED -> mutableInjectedValueOf(value!!)
            ValueStatus.NOT_FOUND -> mutableNotFoundValue()
            ValueStatus.LOADED -> mutableLoadedValueOf(value!!)
        }
    }

}

class MutableLoadedValue<T> : LoadableValue<T>() {

    fun inject(value : T) : MutableLoadedValue<T> {
        this.value = value
        this.status = ValueStatus.INJECTED
        return this
    }

    fun load(loader : () -> T) : MutableLoadedValue<T> {
        this.value = loader.invoke()
        this.status = ValueStatus.LOADED
        return this
    }

    fun unload() : MutableLoadedValue<T> {
        this.value = null
        this.status = ValueStatus.UNLOADED
        return this
    }

    fun notFound() : MutableLoadedValue<T> {
        this.value = null
        this.status = ValueStatus.NOT_FOUND
        return this
    }

    fun ifMutableStatus(status : ValueStatus, action : MutableLoadedValue<T>.() -> Unit): MutableLoadedValue<T> {
        if(this.status == status)
            action.invoke(this)

        return this
    }

    fun ifMutableLoaded(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(isLoaded()) action.invoke(this)
        return this
    }

    fun ifMutableNotLoaded(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(!isLoaded()) action.invoke(this)
        return this
    }

    fun ifMutableInjected(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(isInjected()) action.invoke(this)
        return this
    }

    fun ifMutableUntouched(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(isUntouched()) action.invoke(this)
        return this
    }

    fun ifMutableNotUntouched(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(!isUntouched()) action.invoke(this)
        return this
    }

    fun ifMutableUnloaded(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(isUnLoaded()) action.invoke(this)
        return this
    }

    fun ifMutableNotUnloaded(action: MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(!isUnLoaded()) action.invoke(this)
        return this
    }

    fun ifMutableNotFound(action : MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(isNotFound()) action.invoke(this)
        return this
    }

    fun ifMutableFound(action : MutableLoadedValue<T>.() -> Unit) : MutableLoadedValue<T> {
        if(!isNotFound()) action.invoke(this)
        return this
    }

    fun asLoadResult() : LoadResult<T> {
        return LoadResult(value, status)
    }

}

abstract class LoadableValue<T> {

    var value : T? = null
    protected set
    var status = ValueStatus.UNTOUCHED
    protected set

    fun isLoaded() : Boolean {
        return status == ValueStatus.LOADED
    }

    fun hasValue() : Boolean {
        return value != null
    }

    @Throws(IllegalStateException::class)
    fun aGet() : T {
        if(value == null) if(value == null) throw IllegalStateException("Value not present [status=$status]")
        return value!!
    }


    fun hasNoValue() : Boolean {
        return value == null
    }

    fun ifLoaded(action: (T) -> Unit) : LoadableValue<T> {
        if(isLoaded()) action.invoke(value!!)
        return this
    }

    fun getIfLoadedOrElse(otherValue : () -> T) : T {
        return if(isLoaded()) value!!
        else otherValue.invoke()
    }

    fun ifNotLoaded(action : LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(!isLoaded()) action.invoke(this)
        return this
    }

    fun isInjected() : Boolean {
        return status == ValueStatus.INJECTED
    }

    fun ifInjected(action: (T) -> Unit) : LoadableValue<T> {
        if(isInjected()) action.invoke(value!!)
        return this
    }

    fun ifNotInjected(action : LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(!isInjected()) action.invoke(this)
        return this
    }

    fun isUntouched() : Boolean {
        return status == ValueStatus.UNTOUCHED
    }

    fun ifUntouched(action: LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(isUntouched()) action.invoke(this)
        return this
    }

    fun isUnLoaded() : Boolean {
        return status == ValueStatus.UNLOADED
    }

    fun ifUnloaded(action: LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(isUnLoaded()) action.invoke(this)
        return this
    }

    fun ifNotUnloaded(action : LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(!isUnLoaded()) action.invoke(this)
        return this
    }

    fun isNotFound() : Boolean {
        return status == ValueStatus.NOT_FOUND
    }

    fun ifNotFound(action: LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(isNotFound()) action.invoke(this)
        return this
    }

    fun ifFound(action : LoadableValue<T>.() -> Unit) : LoadableValue<T> {
        if(!isNotFound()) action.invoke(this)
        return this
    }

    fun withValue(action : (T) -> Unit) : LoadableValue<T> {
        if(value == null) throw IllegalStateException("Value not present [status=$status]")
        action.invoke(value!!)
        return this
    }

    fun withValue(action : (ValueStatus, T) -> Unit) : LoadableValue<T> {
        if(value == null) throw IllegalStateException("Value not present [status=$status]")
        action.invoke(status, value!!)
        return this
    }

    fun tryWithValue(action : (T) -> Unit) : LoadableValue<T> {
        if(value != null) action.invoke(value!!)
        return this
    }

    fun tryWithValue(action : (ValueStatus, T) -> Unit) : LoadableValue<T> {
        if(value != null) action.invoke(status, value!!)
        return this
    }

    fun isAtStatus(status : ValueStatus) : Boolean {
        return this.status == status
    }

    fun ifStatus(status: ValueStatus, action : (T?) -> Unit) : LoadableValue<T> {
        if(isAtStatus(status))
            action.invoke(value)
        return this
    }

    fun aWithValue(action : (T?) -> Unit) : LoadableValue<T> {
        action.invoke(value)
        return this
    }

    fun aWithValue(action : (ValueStatus, T?) -> Unit) : LoadableValue<T> {
        action.invoke(status, value)
        return this
    }

    fun <R> map(mapper : (T?) -> R?) : R? {
        return mapper.invoke(value)
    }

    fun <R> aMap(mapper : (T?) -> R) : R {
        return mapper.invoke(value)
    }

    @Throws(IllegalStateException::class)
    fun <R> pMap(mapper : (T) -> R) : R {
        if(value == null) throw IllegalStateException("Unable to map with a $status status")
        return mapper.invoke(value!!)
    }

}

fun <T> mutableLoadableValue() : MutableLoadedValue<T> {
    return MutableLoadedValue()
}

fun <T> mutableLoadedValueOf(value : T) : MutableLoadedValue<T> {
    return mutableLoadableValue<T>().load { value }
}

fun <T> mutableInjectedValueOf(value : T) : MutableLoadedValue<T> {
    return mutableLoadableValue<T>().inject(value)
}

fun <T> mutableNotFoundValue() : MutableLoadedValue<T> {
    return mutableLoadableValue<T>().notFound()
}

fun <T> mutableUnloadedValue() : MutableLoadedValue<T> {
    return mutableLoadableValue<T>().unload()
}

fun <T> loadedResult(value : T) : LoadResult<T> {
    return LoadResult(value, ValueStatus.LOADED)
}

fun <T> notFoundResult() : LoadResult<T> {
    return LoadResult(null, ValueStatus.NOT_FOUND)
}

fun <T> injectedResult(value : T) : LoadResult<T> {
    return LoadResult(value, ValueStatus.INJECTED)
}

fun <T> untouchedResult() : LoadResult<T> {
    return LoadResult(null, ValueStatus.UNTOUCHED)
}

fun <T> unloadedResult() : LoadResult<T> {
    return LoadResult(null, ValueStatus.UNLOADED)
}