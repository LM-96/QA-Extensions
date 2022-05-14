package it.unibo.kactor.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FUNCTION)
annotation class QakContext(
    val contextName : String,
    val contextAddress : String,
    val contextProtocol : String,
    val contextPort : Int,
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class QakContextList(
    vararg val contexts : QakContext
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FUNCTION)
annotation class HostName(
    val hostname : String
)
