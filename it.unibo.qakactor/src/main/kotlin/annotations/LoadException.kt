package it.unibo.kactor.annotations

class LoadException(msg : String?, exception : Exception?) : Exception(msg, exception) {
    constructor(msg : String) : this(msg, null)
    constructor(exception : Exception) : this(null, exception)
}