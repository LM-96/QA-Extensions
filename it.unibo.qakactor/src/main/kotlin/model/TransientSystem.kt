package it.unibo.kactor.model

import it.unibo.kactor.parameters.ReadableParameterMap
import it.unibo.kactor.parameters.immutableParameterMap


data class TransientSystem(
    val hostname : String,
    val contexts : Set<TransientContext>,
    val params : ReadableParameterMap = immutableParameterMap()
) {

    fun getContextsOnHost() : List<TransientContext> {
        return contexts.filter { it.contextAddress == hostname }
    }

    @Throws(NoSuchElementException::class)
    fun getLocalContext() : TransientContext {
        return contexts.first { it.contextAddress == hostname }
    }

    fun getContextWithAddress(address : String) : Set<TransientContext> {
        return contexts.filter { it.contextAddress == address }.toSet()
    }

    @Throws(NoSuchElementException::class)
    fun getContextWithName(name : String) : TransientContext {
        return contexts.first { it.contextName == name }
    }

    @Throws(NoSuchElementException::class)
    fun getContextOf(address: String, port : Int) : TransientContext {
        return contexts.first { it.contextAddress == address && it.contextPort == port }
    }

}