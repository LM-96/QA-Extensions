package it.unibo.kactor.annotations

import it.unibo.kactor.model.TransientStartMode

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class StartMode(
    val mode : TransientStartMode
)
