package it.unibo.kactor.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class MqttBroker(
    val address : String,
    val port : Int,
    val topic : String
)