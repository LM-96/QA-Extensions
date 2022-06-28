package it.unibo.kactor.dsl

import it.unibo.kactor.launchQak
import it.unibo.kactor.parameters.asNameOf
import it.unibo.kactor.parameters.immutableParameterMapOf
import it.unibo.kactor.utils.KnownParamNames
import kotlinx.coroutines.runBlocking

/* DSL ANNOTATION */
@DslMarker
annotation class QActorDsl

@QActorDsl val start get() = run { start() }

/* START */
@QActorDsl fun start() {
    runBlocking {
        launchQak(immutableParameterMapOf(KnownParamNames.BLOCK_ANNOTATIONS asNameOf annotationBlock.orElse(true)))
    }
}
