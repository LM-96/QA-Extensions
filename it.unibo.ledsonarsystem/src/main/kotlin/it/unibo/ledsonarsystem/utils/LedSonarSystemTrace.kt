package it.unibo.ledsonarsystem.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

enum class TraceCmdType {
    ENABLE_TRACE, DISABLE_TRACE, PRINTLN, PRINT
}

enum class TraceState {
    ENABLED, DISABLED
}

data class TraceCmd (
    val type : TraceCmdType,
    val param : Any? = null
)

object LedSonarSystemTrace {

    private val traceStateFlow = MutableStateFlow(TraceState.DISABLED)
    val traceState = traceStateFlow.asStateFlow()

    private val traceActor = GlobalScope.actor<TraceCmd> {
        for(msg in channel) {
            when(msg.type) {

                TraceCmdType.ENABLE_TRACE -> {
                    if(traceStateFlow.value != TraceState.ENABLED) {
                        traceStateFlow.emit(TraceState.ENABLED)
                    }
                }

                TraceCmdType.DISABLE_TRACE -> {
                    if(traceStateFlow.value != TraceState.DISABLED) {
                        traceStateFlow.emit(TraceState.DISABLED)
                    }
                }

                TraceCmdType.PRINTLN -> {
                    if(traceStateFlow.value == TraceState.ENABLED)
                        kotlin.io.println(msg.param as String)
                }

                TraceCmdType.PRINT -> {
                    if(traceStateFlow.value == TraceState.ENABLED)
                        kotlin.io.print(msg.param as String)
                }
            }
        }
    }

    suspend fun enable() {
        traceActor.send(TraceCmd(TraceCmdType.ENABLE_TRACE))
    }

    suspend fun disable() {
        traceActor.send(TraceCmd(TraceCmdType.DISABLE_TRACE))
    }

    suspend fun println(msg : String) {
        traceActor.send(TraceCmd(TraceCmdType.PRINTLN, msg))
    }

    suspend fun print(msg : String) {
        traceActor.send(TraceCmd(TraceCmdType.PRINT, msg))
    }

    fun enabled() : Boolean {
        return traceState.value == TraceState.ENABLED
    }

    fun disabled() : Boolean {
        return traceState.value == TraceState.DISABLED
    }

    fun getState() : TraceState {
        return traceState.value
    }

}

suspend fun sTracePrintln(msg : String) {
    LedSonarSystemTrace.println(msg)
}

fun tracePrintln(msg : String) {
    runBlocking {
        LedSonarSystemTrace.println(msg)
    }
}

suspend fun sTracePrint(msg : String) {
    LedSonarSystemTrace.print(msg)
}

fun tracePrint(msg : String) {
    runBlocking {
        LedSonarSystemTrace.print(msg)
    }
}