package it.unibo.ledsonarsystem.websocket

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.util.*

class WebSocketValue<T>(
    private val host : String,
    private val port : Int,
    private val path : String,
    value : T,
    private val scope : CoroutineScope = GlobalScope,
    private val parser : (String) -> Optional<T>
) {

    private val internalFlow = MutableStateFlow(value)
    val valueFlow = internalFlow.asStateFlow()
    private val closeCmd = Channel<Unit>()

    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    fun start() {
        scope.launch {
            while(closeCmd.tryReceive().isSuccess);
            client.webSocket(
                method = HttpMethod.Get,
                host = this@WebSocketValue.host,
                port = this@WebSocketValue.port,
                path = this@WebSocketValue.path) {
                var closed = false
                while(!closed) {
                    try {
                        select<Unit> {
                            incoming.onReceive {
                                it as Frame.Text
                                val obj = parser.invoke(it.readText())
                                if(obj.isPresent) internalFlow.emit(obj.get())
                            }
                            closeCmd.onReceive {
                                closed = true
                            }
                        }
                    } catch (e : ClosedReceiveChannelException) {
                        closed = true
                    }
                }

            }
        }
    }

    suspend fun stop() {
        closeCmd.send(Unit)
    }


}