package it.unibo.ledsonarsystem.sensors

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import it.unibo.ledsonarsystem.websocket.powerLedCommand
import it.unibo.ledsonarsystem.websocket.toJSON
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.io.Closeable

class WebSocketLed(override val id: String,
                   val host : String,
                   val port : Int,
                   val path : String,
                   private val scope : CoroutineScope = GlobalScope) : AbstractLed(), Closeable, AutoCloseable {

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }
    private val powerCmd = Channel<PowerState>()
    private val closeCmd = Channel<Unit>()

    init {
        scope.launch {
            client.webSocket(
                method = HttpMethod.Get,
                host = this@WebSocketLed.host,
                port = this@WebSocketLed.port,
                path = this@WebSocketLed.path) {
                var closed = false
                try{
                    while(!closed) {
                        select<Unit> {
                            powerCmd.onReceive {
                                send(Frame.Text(powerLedCommand(it).toJSON()))
                            }

                            closeCmd.onReceive {
                                closed = true
                            }
                        }
                    }
                } catch (e : ClosedReceiveChannelException) {
                    closed = true
                }

            }
        }
    }

    override suspend fun powerOnRoutine() {
        powerCmd.send(PowerState.ON)
    }

    override suspend fun powerOffRoutine() {
        powerCmd.send(PowerState.OFF)
    }

    override fun close() {
        client.close()
    }
}