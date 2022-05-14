/*package it.unibo.kactor.demo

import it.unibo.kactor.QActorBasicFsm
import it.unibo.kactor.annotations.*
import it.unibo.kactor.launchQak
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@QActor("demoCtx")
class Receiver : QActorBasicFsm() {

    @State @Initial
    @WhenRequest("t0", "replyToRequest", "whoareyou")
    suspend fun waitForRequest() {
        actorPrintln("waiting for next request...")
    }

    @State
    @EpsilonMove("t1", "waitForRequest")
    suspend fun replyToRequest() {
        actorPrintln("received request: ${currentMsg}")
        answer(currentMsg.msgId(), "whoami", "whoami(${name.uppercase()})")
    }

}

@QActor("demoCtx")
class Sender : QActorBasicFsm() {

    val maxTimes = 3
    var time = 0

    @State
    @Initial
    @WhenReply("t0", "responseReceived", "whoami")
    suspend fun requestName() {
        actorPrintln("i will ask to receiver [time = $time]")
        request("whoareyou", "whoareyou(N)", "Receiver" )
        time++
        actorPrintln("i wait the response")
    }

    @State
    @EpsilonMove("t1", "requestName")
    suspend fun responseReceived() {
        actorPrintln("received response: $currentMsg")
        //actorPrintln("name: ${payloadArg(0)}")
        actorPrintln("i will ask again others ${maxTimes - time} times")
        delay(2000)
    }

    @GuardFor("t1", "exit")
    fun ifTimeReachedUp() : Boolean {
        return time<maxTimes
    }

    @State
    suspend fun exit() {
        actorPrintln("i finished my work")
    }

}

@HostName("localhost")
@QakContext("demoCtx", "localhost", "TCP", 9000)
class ContextConfiguration()

fun main(args : Array<String>) {
    runBlocking {
        launchQak(this)
    }
}*/