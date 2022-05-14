package it.unibo.kactor.builders

class BuildException(msg : String?, exception : Exception?) : Exception(msg, exception) {
    constructor(msg : String) : this(msg, null)
    constructor(exception: Exception?) : this(null, exception)
}