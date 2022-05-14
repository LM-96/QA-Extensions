package it.unibo.kactor.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class EpsilonMove(
    val edgeName : String,
    val targetState : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class EpsilonMoves(
    vararg val epsilonMoves : EpsilonMove
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenDispatch(
    val edgeName : String,
    val targetState : String,
    val messageName : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenDispatches(
    vararg val whenDispatches : WhenDispatch
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenRequest(
    val edgeName : String,
    val targetState : String,
    val messageName : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenRequests(
    vararg val whenRequests : WhenRequest
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenReply(
    val edgeName : String,
    val targetState : String,
    val messageName : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenReplies(
    vararg val whenReplies : WhenReply
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenInvitation(
    val edgeName : String,
    val targetState : String,
    val messageName : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenInvitations(
    vararg val invitations : WhenInvitation
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenEvent(
    val edgeName : String,
    val targetState : String,
    val eventName : String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class WhenEvents(
   vararg val whenEvents : WhenEvent
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class GuardFor(
    val transitionEdgeName : String,
    val elseTarget : String = ""
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class WhenTime(
    val edgeName : String,
    val targetState : String,
    val millis : Long
)