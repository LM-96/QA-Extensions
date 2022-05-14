package it.unibo.kactor.dsl

import it.unibo.kactor.builders.TransitionType

@QActorDsl object StateBodyContinuation
@QActorDsl data class TransitionNameContinuation(val edgeName : String,
                                                 val msgId : String,
                                                 val transitionType: TransitionType)
@QActorDsl data class TransitionGuardContinuation(val edgeName : String,
                                                  val msgId : String?,
                                                  val transitionType: TransitionType,
                                                  val guard : () -> Boolean)
@QActorDsl data class TransitionGuardIfTrueContinuation(val edgeName : String,
                                                        val ifTrueTarget : String,
                                                        val msgId : String?,
                                                        val transitionType: TransitionType,
                                                        val guard : () -> Boolean)

@QActorDsl object transition