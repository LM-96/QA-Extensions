package it.unibo.kactor.model.actorbody.statebody

import it.unibo.kactor.State
import it.unibo.kactor.model.actorbody.TransientStateBody

open class TransientLambdaStateBody(
    override val action : suspend State.() -> Unit
) : TransientStateBody