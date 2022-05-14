package it.unibo.kactor.model

import it.unibo.kactor.builders.TransitionType

data class TransitentTransition(
    val edgeName: String,
    val targetState: String,
    val transitionType : TransitionType,
    val msgCondName : String? = null,
    val guard : (() -> Boolean)? = null,
    val millis : Long? = null,
    val elseTargetState : String? = null
) {
    init {
        when(transitionType) {
            TransitionType.WHEN_REQUEST, TransitionType.WHEN_REPLY, TransitionType.WHEN_DISPATCH,
            TransitionType.WHEN_EVENT, TransitionType.WHEN_REQUEST_GUARDED, TransitionType.WHEN_REPLY_GUARDED,
            TransitionType.WHEN_DISPATCH_GUARDED, TransitionType.WHEN_EVENT_GUARDED ->
            {
                if(msgCondName == null)
                    throw IllegalArgumentException("Invalid transition [$edgeName]: msgCondName cannot be null with transition type $transitionType")
            }

            TransitionType.WHEN_TIMEOUT -> {
                if(millis == null)
                    throw IllegalArgumentException("Invalid transition [$edgeName]: millis cannot be null with transition type $transitionType")
            }
        }
    }
}