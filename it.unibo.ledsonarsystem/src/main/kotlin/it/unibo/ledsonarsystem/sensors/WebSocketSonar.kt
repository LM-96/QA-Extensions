package it.unibo.ledsonarsystem.sensors

import it.unibo.ledsonarsystem.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.*

class WebSocketSonar(override val id: String,
                     val host : String,
                     val port : Int,
                     val path : String,
                     private val scope : CoroutineScope = GlobalScope
) : AbstractSonar(), Closeable, AutoCloseable {

    private val value = WebSocketValue(host, port, path, 5000, scope) { msg ->
        val rawCmd = msg.parseWebSocketCommand()
        if(rawCmd.isPresent) {
            if (rawCmd.get().isDistance()) {
                return@WebSocketValue Optional.of(rawCmd.get().asDistanceCommand().value)
            }
        }
        return@WebSocketValue Optional.empty()
    }

    val valueFlow = value.valueFlow

    init {
       value.start()
    }

    override suspend fun read(): Int {
        return value.valueFlow.value
    }

    override fun close() {
        runBlocking { value.stop() }
    }

}