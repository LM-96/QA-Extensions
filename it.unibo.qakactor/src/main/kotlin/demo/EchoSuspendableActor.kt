package it.unibo.kactor.demo

import it.unibo.kactor.*
import it.unibo.kactor.annotations.HostName
import it.unibo.kactor.annotations.QActor
import it.unibo.kactor.annotations.Tracing
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

@QActor("sContextTest")
class EchoSuspendableActor(name : String) : SuspendableActorBasicFsm(name) {

    override fun getBody(): ActorBasicFsm.() -> Unit {
        return {
            state("begin") {
                action {
                    actorPrintln("hello :)")
                }
                transition("t0", "work", doswitch())
            }
            state("work"){
                action {
                    actorPrintln("working")
                }
                transition("t1", "echoReply", whenRequest("echoRequest"))
            }
            state("echoReply") {
                action {
                    delay(200)
                    actorPrintln("Received request: $currentMsg")
                    answer("echoRequest", "echoResponse", currentMsg.msgContent())
                }
                transition("t2", "work", doswitch())
            }
        }
    }

    override fun getInitialState(): String {
        return "begin"
    }
}


@it.unibo.kactor.annotations.QakContext("sContextTest",
    "localhost", "TCP", 9000)
@HostName("localhost")
@Tracing
class ContextConfiguration

fun main(args : Array<String>) {
    runBlocking {
        launchQak(this)
        delay(2000)
        var msg : IApplMessage
        var msg1 : IApplMessage
        var msg2 : IApplMessage
        var msg3 : IApplMessage
        var echoActor = QakContext.getActor("echosuspendableactor")
        while(echoActor == null) {
            print("Unable to find echoactor. Please retype the name of the actor: ")
            echoActor = QakContext.getActor(readLine()!!.trim().uppercase())
        }

        var choice = menu()
        var line : String
        while(choice != 7) {
            when(choice) {
                1 -> {//send echo message
                    print("Enter the content: ")
                    line = readLine()!!
                    msg = MsgUtil.buildRequest("main", "echoRequest", line, "echosuspendableactor")
                    MsgUtil.sendMsg(msg, echoActor)
                }
                2 -> {//send three echo messages
                    msg1 = MsgUtil.buildRequest("main", "echoRequest", "msg1", "echosuspendableactor")
                    msg2 = MsgUtil.buildRequest("main", "echoRequest", "msg2", "echosuspendableactor")
                    msg3 = MsgUtil.buildRequest("main", "echoRequest", "msg3", "echosuspendableactor")
                    MsgUtil.sendMsg(msg1, echoActor)
                    MsgUtil.sendMsg(msg2, echoActor)
                    MsgUtil.sendMsg(msg3, echoActor)
                }
                3 -> {//send suspend message
                    println("Sending suspend message...")
                    msg = MsgUtil.buildSuspendMessage("main", "echosuspendableactor")
                    MsgUtil.sendMsg(msg, echoActor)
                }
                4 -> {//send resume message
                    println("Sending resume message...")
                    msg = MsgUtil.buildResumeMessage("main", "echosuspendableactor")
                    MsgUtil.sendMsg(msg, echoActor)
                    delay(1000)
                }
                5 -> {//send resume and then immediately send random messages
                    msg1 = MsgUtil.buildRequest("main", "echoRequest", "random1", "echosuspendableactor")
                    msg2 = MsgUtil.buildRequest("main", "echoRequest", "random2", "echosuspendableactor")
                    println("Sending resume message...")
                    msg = MsgUtil.buildResumeMessage("main", "echosuspendableactor")
                    MsgUtil.sendMsg(msg, echoActor)
                    println("Sending random messages...")
                    MsgUtil.sendMsg(msg1, echoActor)
                    MsgUtil.sendMsg(msg2, echoActor)
                    delay(1000)
                }
                6 -> {
                    println("Sending resume message...")
                    msg = MsgUtil.buildResumeMessage("main", "echosuspendableactor")
                    MsgUtil.sendMsg(msg, echoActor)
                    println("Sending suspend message...")
                    msg = MsgUtil.buildSuspendMessage("main", "echosuspendableactor")
                    MsgUtil.sendMsg(msg, echoActor)

                }
                else -> {
                    println("Unexpected choice [$choice]. Try again")
                }
            }
            delay(500)
            choice = menu()
        }
        exitProcess(0)
    }
}

private fun printMenu() {
    println("Options:")
    println("\t[1]: send echo message")
    println("\t[2]: send three echo messages")
    println("\t[3]: send suspend message")
    println("\t[4]: send resume message")
    println("\t[5]: send resume and then immediately send random messages")
    println("\t[6]: send resume and immediately suspend")
    println("\t[7]: exit")
    print("Type your choice: ")
}

private fun menu() : Int {
    var choice : Int = -1
    while(choice <= 0 || choice > 7) {
        printMenu()
        try {
            choice = readLine()!!.trim().toInt()
        } catch (nfe : NumberFormatException) {
            println("Invalid input. Please try again")
        }
        if(choice <= 0 || choice > 7)
            println("Invalid choice number")
    }

    return choice
}