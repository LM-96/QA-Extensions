package it.unibo.kactor.parameters

/**
 * Represents an object that holds a public [ReadableParameterMap] in order to
 * retrieve parameters for some application scopes
 */
interface ReadableOnlyParametersOwner {

    val readOnlyParameters : ReadableParameterMap

}