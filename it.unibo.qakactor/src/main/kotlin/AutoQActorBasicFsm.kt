package it.unibo.kactor

abstract class AutoQActorBasicFsm : QActorBasicFsm() {

    abstract fun getBody(): (ActorBasicFsm.() -> Unit)
    abstract fun getInitialState(): String

}