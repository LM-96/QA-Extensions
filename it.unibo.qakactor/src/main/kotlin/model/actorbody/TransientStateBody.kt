package it.unibo.kactor.model.actorbody

import it.unibo.kactor.State

interface TransientStateBody {
    val action : suspend State.() -> Unit
}