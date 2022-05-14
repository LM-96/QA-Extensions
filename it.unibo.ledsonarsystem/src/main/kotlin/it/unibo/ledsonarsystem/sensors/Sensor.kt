package it.unibo.ledsonarsystem.sensors

enum class SensorType {
    LED, SONAR
}

interface Sensor : Identifiable {

    val sensorType : SensorType

}