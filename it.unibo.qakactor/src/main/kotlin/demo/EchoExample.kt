package it.unibo.kactor.demo

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.builders.SystemBuilder
import kotlinx.coroutines.runBlocking

fun main(args : Array<String>) {
    /* BODIES OF THE STATES FOR echiactor ***************************** */
    val s0Body : suspend QActorBasicFsm.()-> Unit =
        { println("started") }
    val workBody : suspend QActorBasicFsm.() -> Unit =
        { println("idle") }
    val handleRequestBody : suspend QActorBasicFsm.() -> Unit =
        {
            answer("echorequest", "echoreply", currentMsg.msgContent())
        }

    /* SYSTEM BUILDER ********************************************************* */
    val sysBuilder = SystemBuilder()

    /* SYSTEM CREATION ******************************************************** */
    val system = runBlocking {
        sysBuilder.addHostname("localhost").addScope(this)
            .newContext()
            .addContextName("ctxecho")
            .addContextAddress("localhost").addContextPort(9000)
            .addContextProtocol("TCP")
            .newActorBasic().addActorName("echoactor")
            .upgrateToFsmBuilder().addQActorBasicFsm(QActorBasicFsm())
            .newState().addStateName("s0").addStateBody(s0Body)
            .newTransition()
            .addEdgeName("t0").addTargetState("work")
            .buildEpsilonMove().buildState()
            .setInitialState("s0")
            .newState().addStateName("work").addStateBody(workBody)
            .newTransition()
            .addEdgeName("t1").addTargetState("handleRequest")
            .buildWhenRequest("echorequest").buildState()
            .newState().addStateName("handleRequest").addStateBody(handleRequestBody)
            .newTransition()
            .addEdgeName("t2").addTargetState("work").buildEpsilonMove()
            .buildState()
            .buildInContext().second.buildInSystem().second.build()
    }

    println(system)
}