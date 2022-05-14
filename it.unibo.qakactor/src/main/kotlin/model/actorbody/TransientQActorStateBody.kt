package it.unibo.kactor.model.actorbody

import it.unibo.kactor.QActorBasicFsm

open class TransientQActorStateBody(
    qActorBody : suspend QActorBasicFsm.() -> Unit,
    val qActorBasicFsm: QActorBasicFsm = QActorBasicFsm()
) : TransientLambdaStateBody({ qActorBody.invoke(qActorBasicFsm) }), TransientStateBody