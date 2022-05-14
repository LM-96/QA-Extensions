package it.unibo.kactor.annotations

import it.unibo.kactor.ApplMessageType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Message(
    val messageType : ApplMessageType,
    val messageName : String,
    val argNames : Array<String>
)
