package it.unibo.ledsonarsystem.sensors

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

enum class PowerState {
    ON, OFF
}

abstract class AbstractPowerableSensor(autoPowerOn : Boolean = false) : Sensor {

    private val poweredFlow = MutableStateFlow(
        if(autoPowerOn) PowerState.ON else PowerState.OFF
    )
    val poweredSharedFlow = poweredFlow.asStateFlow()

    protected abstract suspend fun powerOnRoutine()
    protected abstract suspend fun powerOffRoutine()

    suspend fun powerOn() {
        powerOnRoutine()
        poweredFlow.emit(PowerState.ON)
    }

    suspend fun powerOff() {
        powerOffRoutine()
        poweredFlow.emit(PowerState.OFF)
    }

    suspend fun switch() {
        if(isPoweredOn())
            powerOff()
        else
           powerOn()
    }

    fun isPoweredOn() : Boolean {
        return poweredFlow.value == PowerState.ON
    }

    fun isPoweredOff() : Boolean {
        return poweredFlow.value == PowerState.OFF
    }

    suspend fun waitForPowerOn() {
        waitFor(PowerState.ON)
    }

    suspend fun waitForPowerOff() {
        waitFor(PowerState.OFF)
    }

    suspend fun waitFor(powerState: PowerState) {
        poweredFlow.first { it == powerState }
    }

}