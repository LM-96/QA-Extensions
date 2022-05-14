package it.unibo.kactor.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class QActor(
    val contextName : String,
    val actorName : String = "",
    val discardMessage : Boolean = false,
    val confined : Boolean =  false,
    val ioBound : Boolean = false,
    val channelSize : Int = 50
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RemoteQActor(
    val contextName : String,
    val actorName : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class QActorList(
    vararg val actors : RemoteQActor
)