package it.unibo.kactor.model

import it.unibo.kactor.model.actorbody.TransientStateBody

data class TransientState(
    val stateName : String,
    val stateBody : TransientStateBody,
    val transitions : List<TransitentTransition>
)