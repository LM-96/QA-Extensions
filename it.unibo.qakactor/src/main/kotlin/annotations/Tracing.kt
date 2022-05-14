package it.unibo.kactor.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Tracing(
    val active : Boolean = true
)
