package it.unibo.kactor.model.actorbody

import it.unibo.kactor.State

open class TransientLambdaStateBody(
    override val action : suspend State.() -> Unit
) : TransientStateBody