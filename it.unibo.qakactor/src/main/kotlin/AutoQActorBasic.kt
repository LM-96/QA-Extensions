package it.unibo.kactor

abstract class AutoQActorBasic : QActorBasic() {

    abstract suspend fun actorBody(msg : IApplMessage)

}