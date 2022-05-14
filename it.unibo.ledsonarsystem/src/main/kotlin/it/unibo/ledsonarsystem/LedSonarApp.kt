/*package it.unibo.ledsonardemo

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import it.unibo.ledsonardemo.sensors.WebSocketLed
import it.unibo.ledsonardemo.sensors.WebSocketSonar
import it.unibo.ledsonardemo.websocket.WebSocketConnection
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.util.*
import kotlin.collections.LinkedHashSet

val HTML_SOURCE_FOLDER = object {}.javaClass.classLoader.getResource("html")

fun main() {

    if(HTML_SOURCE_FOLDER == null)
        throw IllegalArgumentException("Unable to find the HTML folder")

    val connections = Collections.synchronizedSet<WebSocketConnection?>(LinkedHashSet())

    val appEngine = embeddedServer(Netty, port = 8000) {
        install(WebSockets)

        val appEngine = routing {
            static("/") {
                staticBasePackage = "html"
                resources(".")
            }

            webSocket("/ledsonarws") {
                val thisConnection = WebSocketConnection(this)
                connections += thisConnection
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val receivedText = frame.readText()
                            println("\t\t* WebSocket | Received text: \'$receivedText\'")
                            connections.forEach {
                                if(it.name != thisConnection.name) {
                                    it.session.send(receivedText)
                                }
                            }
                        }
                    }
                }
                connections -= thisConnection
            }
        }
    }.start()
    runBlocking {
        val led = WebSocketLed("LED0", "localhost", 8000, "/ledsonarws", )
        val sonar = WebSocketSonar("SON0", "localhost", 8000, "/ledsonarws", )

        var choice : String
        do {
            choice = checkedInput("Insert \'ON\' or \'OFF\' to power on/off the led or \'EXIT\' to exit: ",
                "ON", "OFF", "EXIT")
            when(choice) {
                "ON" -> led.powerOn()
                "OFF" -> led.powerOff()
            }
            delay(200)

        } while (choice != "EXIT")
        led.close()
        appEngine.stop()
        println("System closed")
    }

}

fun checkedInput(reqMsg : String, vararg values : String) : String {
    print("${reqMsg.trim()} ")
    var input = readLine()!!
    while(!values.contains(input)) {
        print("Invalid value. Please insert one of these values: ${values.joinToString(",")}\n\tType a new choice: ")
        input = readLine()!!
    }

    return input
}*/
