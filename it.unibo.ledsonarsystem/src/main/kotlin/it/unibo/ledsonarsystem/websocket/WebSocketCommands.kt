package it.unibo.ledsonarsystem.websocket

import com.google.gson.Gson
import it.unibo.ledsonarsystem.sensors.PowerState
import java.util.*

data class WebSocketCommand(
    val cmd : String,
    val value : String
)

data class WebSocketTypedCommand<T>(
    val cmd : String,
    val value : T
)

val DISTANCE_CMD = "DISTANCE_CMD"
val LED_CMD = "LED_CMD"
val TRESHOLD_CMD = "TRESHOLD_CMD"
private val gson = Gson()

fun WebSocketCommand.isDistance() : Boolean {
    return this.cmd == DISTANCE_CMD
}

fun WebSocketCommand.toJSON() : String {
    return gson.toJson(this)
}

fun String.parseWebSocketCommand() : Optional<WebSocketCommand> {
    return try {
        Optional.of(gson.fromJson(this, WebSocketCommand::class.java))
    } catch (e : Exception) {
        Optional.empty()
    }
}

fun WebSocketCommand.asDistanceCommand() : WebSocketTypedCommand<Int> {
    if(!isDistance()) throw IllegalArgumentException("This command is not for distance")
    return WebSocketTypedCommand(this.cmd, this.value.toInt())
}

fun WebSocketCommand.isLed() : Boolean {
    return this.cmd == LED_CMD
}

fun WebSocketCommand.asLedCommand() : WebSocketTypedCommand<PowerState> {
    if(!isLed()) throw IllegalArgumentException("This command is not for led")
    return WebSocketTypedCommand(this.cmd, PowerState.valueOf(this.value))
}

fun WebSocketCommand.isTreshold() : Boolean {
    return this.cmd == TRESHOLD_CMD
}

fun WebSocketCommand.asTresholdCommand() : WebSocketTypedCommand<Int> {
    if(!isTreshold()) throw IllegalArgumentException("This command is not for treshold")
    return WebSocketTypedCommand(this.cmd, this.value.toInt())
}

fun WebSocketTypedCommand<*>.toWebSocketCommand() : WebSocketCommand {
    return WebSocketCommand(this.cmd, this.value.toString())
}

fun powerOnLedCommand() : WebSocketCommand {
    return powerLedCommand(PowerState.ON)
}

fun powerOffLedCommand() : WebSocketCommand {
    return powerLedCommand(PowerState.OFF)
}

fun powerLedCommand(powerState: PowerState) : WebSocketCommand {
    return if(powerState == PowerState.ON)
        WebSocketCommand(LED_CMD, "1")
    else
        WebSocketCommand(LED_CMD, "0")
}