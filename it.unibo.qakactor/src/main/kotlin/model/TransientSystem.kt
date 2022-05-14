package it.unibo.kactor.model

import it.unibo.kactor.utils.ReadableParameterMap
import it.unibo.kactor.utils.immutableParameterMap


data class TransientSystem(
    val hostname : String,
    val contexts : Set<TransientContext>,
    val params : ReadableParameterMap = immutableParameterMap()
) {

    fun getContextsOnHost() : List<TransientContext> {
        return contexts.filter { it.contextAddress == hostname }
    }

}