package it.unibo.kactor.model.actorbody

import it.unibo.kactor.ActorBasicFsm

abstract class TransientActorBasicFsmBody(
    val body : ActorBasicFsm.() -> Unit,
    val initialState : String
) : TransientActorBasicBody