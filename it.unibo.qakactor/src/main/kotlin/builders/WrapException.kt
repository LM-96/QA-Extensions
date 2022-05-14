package it.unibo.kactor.builders

class WrapException(msg : String?, exception : Exception?) : Exception(msg, exception) {
    constructor(msg : String) : this(msg, null)
    constructor(exception: Exception?) : this(null, exception)
}