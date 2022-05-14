package it.unibo.ledsonarsystem.sensors

interface OutputSensor<T> : Sensor {

    suspend fun write(value : T)

}