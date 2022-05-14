package it.unibo.kactor.dsl

import it.unibo.kactor.annotations.LoadException
import it.unibo.kactor.builders.sysBuilder
import it.unibo.kactor.utils.KnownParamNames
import it.unibo.kactor.utils.lateSingleInit
import kotlinx.coroutines.CoroutineScope

private var systemSet = false

@QActorDsl object system
@QActorDsl data class SystemHostnameContinuation(val hostname: String)

internal val annotationBlock = lateSingleInit<Boolean>()


@QActorDsl
infix fun system.hostname(hostname : String) : SystemHostnameContinuation {
    if(systemSet)
        throw LoadException("Hostname is already set")
    sysBuilder.addHostname(hostname).addGlobalScope()
    systemSet = true
    return SystemHostnameContinuation(hostname)
}

@QActorDsl
infix fun SystemHostnameContinuation.scope(scope : CoroutineScope) {
    sysBuilder.addScope(scope)
}

private fun enableMsgLogging() {
    sysBuilder.addParameter(KnownParamNames.MSG_LOGGING, true)
    println("          %%% dsl | MsgLogging enabled")
}

private fun enableTrace() {
    sysBuilder.addParameter(KnownParamNames.TRACE, true)
    println("          %%% dsl | Trace enabled")
}

/* MQTT BROKER */
@QActorDsl object mqttBroker
@QActorDsl class MqttBrokerAddressContinuation(val address: String)
@QActorDsl class MqttBrokerPortContinuation(val address: String, val port : Int)
@QActorDsl class MqttBrokerTopicContinuation(val address: String, val port : Int, val topic : String)

@QActorDsl
infix fun mqttBroker.ip(address : String) : MqttBrokerAddressContinuation {
    sysBuilder.addParameter(KnownParamNames.MQTT_IP, address)
    return MqttBrokerAddressContinuation(address)
}

@QActorDsl
infix fun MqttBrokerAddressContinuation.port(port : Int) : MqttBrokerPortContinuation {
    sysBuilder.addParameter(KnownParamNames.MQTT_PORT, port)
    return MqttBrokerPortContinuation(address, port)
}

@QActorDsl
infix fun MqttBrokerPortContinuation.topic(topic : String) : MqttBrokerTopicContinuation {
    sysBuilder.addParameter(KnownParamNames.MQTT_TOPIC, topic)
    return MqttBrokerTopicContinuation(address, port, topic)
}

/* TRACE, MSGLOGGING AND ANNOTATIONS */
@QActorDsl object set
@QActorDsl object enabled
@QActorDsl object disabled

@QActorDsl
infix fun set.trace(enabled : enabled) {
    sysBuilder.addParameter(KnownParamNames.TRACE, true)
}

@QActorDsl
infix fun set.trace(disabled : disabled) {
    sysBuilder.addParameter(KnownParamNames.TRACE, false)
}

@QActorDsl
infix fun set.msgLogging(enabled : enabled) {
    sysBuilder.addParameter(KnownParamNames.MSG_LOGGING, true)
}

@QActorDsl
infix fun set.msgLogging(disabled : disabled) {
    sysBuilder.addParameter(KnownParamNames.MSG_LOGGING, false)
}

@QActorDsl
infix fun set.annotations(enabled: enabled) {
    if(annotationBlock.isInitialized())
        throw LoadException("Annotation enabling is already configured")
    annotationBlock.set(false)
}