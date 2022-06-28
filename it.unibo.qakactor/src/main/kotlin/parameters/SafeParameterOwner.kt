package it.unibo.kactor.parameters

/**
 * Represents an object that holds two types of parameter map:
 * * a [ReadableParameterMap] that is *read-only*
 * * a [MutableParameterMap] that let external entities to store parameters
 */
interface SafeParameterOwner : ReadableOnlyParametersOwner, MutableParameterOwner