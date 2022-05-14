package it.unibo.kactor.model.actorbody

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.utils.invokeSuspend
import java.lang.reflect.Method

class TransientQActorMethodStateBody(
    qActorMethod : Method,
    val qActorBasicFsm : QActorBasicFsm
) : TransientLambdaStateBody({qActorMethod.invokeSuspend(qActorBasicFsm)})