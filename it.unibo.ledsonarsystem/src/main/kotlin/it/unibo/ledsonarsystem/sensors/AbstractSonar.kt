package it.unibo.ledsonarsystem.sensors

abstract class AbstractSonar : InputSensor<Int> {

    override val sensorType = SensorType.SONAR

}