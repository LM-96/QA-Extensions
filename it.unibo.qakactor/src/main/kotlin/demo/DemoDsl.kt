/*package it.unibo.kactor.demo

import it.unibo.kactor.dsl.*
import it.unibo.kactor.QActorBasic.*
import kotlinx.coroutines.delay

fun main(args : Array<String>) {

    system hostname "localhost"
    mqttBroker ip "mqttIp" port 1020 topic "topic"
    //set annotations enabled
    set msgLogging enabled
    set trace enabled

    context name "ctxDemo" host "localhost" port 9000 withActors {
        Qactor name "SenderDSL" definedBy {

            val maxTimes = 3
            var time = 0

            initialState name "requestName" withBody {
                actorPrintln("i will ask to receiver [time = $time]")
                send request "whoareyou" to "ReceiverDSL" withArgs "N"
                time++
                actorPrintln("i wait the response")
            } with transitions [
                    -"t0" whenReply "whoami" goto "responseReceived"
            ]

            state name "responseReceived" withBody {
                actorPrintln("received response: $currentMsg")
                actorPrintln("name: ${payloadArg(0)}")
                actorPrintln("i will ask again others ${maxTimes - time} times")
                delay(2000)
            } with transitions [
                -"t0" withGuard {time < maxTimes} then "requestName" otherwise "exit"
            ]

            state name "exit" withBody {
                actorPrintln("i finished my work")
            }
        }

        Qactor name "ReceiverDSL" definedBy {

            initialState name "waitForRequest" withBody {
                actorPrintln("waiting for next request...")
            } with transitions [
                -"t0" whenRequest "whoareyou" goto "replyToRequest"
            ]

            state name "replyToRequest" withBody {
                actorPrintln("received request: $currentMsg")
                replyTo request "whoareyou" with "whoami" withArgs name
            } with transitions [
                -"t1" goto "waitForRequest"
            ]
        }
    }

    start
}*/
