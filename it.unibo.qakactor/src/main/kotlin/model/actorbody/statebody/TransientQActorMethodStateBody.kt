package it.unibo.kactor.model.actorbody.statebody

import it.unibo.kactor.IQActorBasicFsm
import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.State
import it.unibo.kactor.qakActorFsm
import it.unibo.kactor.utils.invokeSuspend
import java.lang.reflect.Method

class TransientQActorMethodStateBody(
    qActorMethod : Method,
    val qActorBasicFsm : IQActorBasicFsm
) : TransientLambdaStateBody({qActorMethod.invokeSuspend(qActorBasicFsm.instance.get())})