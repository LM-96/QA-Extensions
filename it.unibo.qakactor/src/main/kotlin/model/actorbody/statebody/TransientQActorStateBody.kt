package it.unibo.kactor.model.actorbody.statebody

import it.unibo.kactor.IQActorBasicFsm
import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.model.actorbody.TransientStateBody

open class TransientQActorStateBody(
    qActorBody : suspend IQActorBasicFsm.() -> Unit,
    val qActorBasicFsm: IQActorBasicFsm = QActorBasicFsm()
) : TransientLambdaStateBody({ qActorBody.invoke(qActorBasicFsm.instance.get() as IQActorBasicFsm) }), TransientStateBody {

}