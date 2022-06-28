package it.unibo.kactor.annotations

import kotlinx.coroutines.CoroutineScope

val CUSTOM_SCOPES = mutableMapOf<String, CoroutineScope>()

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CustomScope(
    val scope : String
)
