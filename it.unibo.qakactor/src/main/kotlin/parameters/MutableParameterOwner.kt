package it.unibo.kactor.parameters

/**
 * Represents an object that holds a public [MutableParameterMap] in which
 * it is possible to store parameters for some application scopes
 */
interface MutableParameterOwner {

    val mutableParameters : MutableParameterMap

}