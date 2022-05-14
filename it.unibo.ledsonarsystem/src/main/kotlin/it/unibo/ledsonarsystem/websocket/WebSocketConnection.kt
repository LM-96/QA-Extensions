package it.unibo.ledsonarsystem.websocket

import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.concurrent.atomic.AtomicInteger

class WebSocketConnection(val session: DefaultWebSocketSession) : Closeable, AutoCloseable {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val name = "user${lastId.getAndIncrement()}"

    override fun close() {
        runBlocking {
            session.close(CloseReason(CloseReason.Codes.NORMAL, "System closed"))
        }
    }
}