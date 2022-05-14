package it.unibo.ledsonarsystem.sensors

interface InputSensor<T> : Sensor {

    suspend fun read() : T
}