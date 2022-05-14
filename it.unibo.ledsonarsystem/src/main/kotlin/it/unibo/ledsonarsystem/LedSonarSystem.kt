package it.unibo.ledsonarsystem

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import it.unibo.ledsonarsystem.sensors.WebSocketLed
import it.unibo.ledsonarsystem.sensors.WebSocketSonar
import it.unibo.ledsonarsystem.utils.LedSonarSystemTrace
import it.unibo.ledsonarsystem.utils.sTracePrintln
import it.unibo.ledsonarsystem.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.*
import kotlin.collections.LinkedHashSet

val DEFAULT_HOST = "localhost"
val DEFAULT_PORT = 8000
val DEFAULT_WS_PATH = "/ledsonarws"
val TRACE = LedSonarSystemTrace

val SYSTEM_SONAR : WebSocketSonar
get() {
    if(LedSonarSystem.SYSTEM_SONAR == null)
        throw IllegalStateException("System is not started. Please start the system before")
    return LedSonarSystem.SYSTEM_SONAR!!
}

val SYSTEM_LED : WebSocketLed
    get() {
        if(LedSonarSystem.SYSTEM_LED == null)
            throw IllegalStateException("System is not started. Please start the system before")
        return LedSonarSystem.SYSTEM_LED!!
}

val TRESHOLD : WebSocketValue<Int>
get() {
    if(LedSonarSystem.TRESHOLD == null)
        throw IllegalStateException("System is not started. Please start the system before")
    return LedSonarSystem.TRESHOLD!!
}

private data class SystemCmd(
    val cmd : String,
    val params: MutableMap<String, Any> = mutableMapOf()
)

enum class SystemStatus {
    IDLE, STARTED, CLOSED
}

object LedSonarSystem : Closeable, AutoCloseable {

    var HOST = "localhost"
    private set

    var PORT = 8000
    private set

    var WS_PATH = "/ledsonarws"
    private set

    var SYSTEM_LED : WebSocketLed? = null
    private set

    var SYSTEM_SONAR : WebSocketSonar? = null
    private set

    var TRESHOLD : WebSocketValue<Int>? = null
    private set

    private val stateFlow = MutableStateFlow(SystemStatus.IDLE)
    private val connections = Collections.synchronizedSet<WebSocketConnection?>(LinkedHashSet())
    private var appEngine : NettyApplicationEngine? = null

    private val systemChan = Channel<SystemCmd>()
    private val systemActor = GlobalScope.actor<SystemCmd>{
        var close = false
        var msg : SystemCmd
        var status = SystemStatus.IDLE
        while(!close) {
            msg = systemChan.receive()
            when(msg.cmd) {
                "start" -> {
                    if(status != SystemStatus.CLOSED && status != SystemStatus.STARTED) {
                        try {
                            val host = msg.params["host"] as String
                            val port = msg.params["port"] as Int
                            val wsPath = msg.params["path"] as String
                            doStart(host, port, wsPath)
                            status = SystemStatus.STARTED
                            stateFlow.emit(SystemStatus.STARTED)
                            println("** LedSonarSystem | Started. Please go to: http://$host:$port/index.html")
                            println("** LedSonarSystem | Websocket url: ws://$host:$port$wsPath")
                        } catch (e : Exception) {
                            println("** LedSonarSystem | Error starting system:")
                            e.printStackTrace()
                        }
                    }
                }
                "stop" -> {
                    if(status != SystemStatus.CLOSED && status != SystemStatus.CLOSED) {
                        try {
                            doStop()
                            stateFlow.emit(SystemStatus.IDLE)
                        } catch (e : Exception) {
                            println("** LedSonarSystem | Error stopping system:")
                            e.printStackTrace()
                        }
                    }
                }
                "close" -> {
                    if(status != SystemStatus.CLOSED) {
                        if(status == SystemStatus.STARTED) {
                            doStop()
                        }
                        stateFlow.emit(SystemStatus.CLOSED)
                        close = true
                    }
                }
            }
        }
        systemChan.close()
    }

    @Throws(IllegalStateException::class)
    suspend fun start(host: String = DEFAULT_HOST, port: Int = DEFAULT_PORT,
                      path: String = DEFAULT_WS_PATH, wait: Boolean = false) {
        try {
            if(stateFlow.value == SystemStatus.STARTED)
                throw IllegalStateException("System already started")
            systemChan.send(SystemCmd("start", mutableMapOf(
                "host" to host, "port" to port, "path" to path, "wait" to wait
            )))
            stateFlow.first { it == SystemStatus.STARTED }
        } catch (e : Exception) {
            throw IllegalStateException("Unable to start: maybe the system has been closed", e)
        }
        if(wait) {
            stateFlow.first { it != SystemStatus.STARTED }
        }

    }

    @Throws(IllegalStateException::class)
    suspend fun restart(wait : Boolean = false) {
        start(HOST, PORT, WS_PATH, wait)
    }

    suspend fun stop() {
            systemChan.send(SystemCmd("stop"))
    }

    private suspend fun doStart(host: String, port : Int, path : String) {
        HOST = host
        PORT = port
        WS_PATH = path

        appEngine = embeddedServer(Netty, port = PORT) {
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
                                sTracePrintln("\t\t* WebSocket | Received text: \'$receivedText\'")
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

        SYSTEM_SONAR = WebSocketSonar("SYS_SONAR", HOST, PORT, WS_PATH)
        SYSTEM_LED = WebSocketLed("SYS_LED", HOST, PORT, WS_PATH)
        TRESHOLD = WebSocketValue(host, port, WS_PATH, 2000, GlobalScope) { msg ->
            val rawCmd = msg.parseWebSocketCommand()
            if(rawCmd.isPresent) {
                if (rawCmd.get().isTreshold()) {
                    return@WebSocketValue Optional.of(rawCmd.get().asTresholdCommand().value)
                }
            }
            return@WebSocketValue Optional.empty()
        }
    }

    private fun doStop() {
        val iterator = connections.iterator()
        var curr : WebSocketConnection

        while (iterator.hasNext()) {
            curr = iterator.next()
            curr.close()
            iterator.remove()
        }
        SYSTEM_LED?.close()
        SYSTEM_SONAR?.close()
        runBlocking { TRESHOLD?.stop() }
        SYSTEM_LED = null
        SYSTEM_SONAR = null
        appEngine?.stop()
    }

    override fun close() {
        runBlocking { systemChan.send(SystemCmd("close")) }
    }

}

suspend fun coStartLedSonarSystem(host : String = DEFAULT_HOST,
                                port: Int = DEFAULT_PORT,
                                path: String = DEFAULT_WS_PATH, wait : Boolean = false) {
    LedSonarSystem.start(host, port, path, wait)
}

fun startLedSonarSystem(host : String = DEFAULT_HOST,
                        port: Int = DEFAULT_PORT,
                        path: String = DEFAULT_WS_PATH,
                        wait : Boolean = false) {
    runBlocking { LedSonarSystem.start(host, port, path, wait) }
}

suspend fun coStopLedSonarSystem() {
    LedSonarSystem.stop()
}

fun stopLedSonarSystem() {
    runBlocking { LedSonarSystem.stop() }
}

fun closeLedSonarSystem() {
    LedSonarSystem.close()
}