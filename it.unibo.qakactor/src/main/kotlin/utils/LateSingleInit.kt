package it.unibo.kactor.utils

class LateSingleInit<T> {

    private var value : T? = null

    @Throws(IllegalStateException::class)
    fun set(initValue : T) {
        if(this.value != null)
            throw IllegalStateException("Value already initialized")

        this.value = initValue
    }

    @Throws(IllegalStateException::class)
    fun get() : T {
        if(value == null)
            throw IllegalStateException("Value not initialized")

        return value!!
    }

    fun orElse(elseObj : T) : T {
        if(value != null)
            return value!!

        return elseObj
    }

    fun isInitialized() : Boolean {
        return value != null
    }

    fun isNotInitialized() : Boolean {
        return value == null
    }

}

fun <T> lateSingleInit() : LateSingleInit<T> {
    return LateSingleInit()
}