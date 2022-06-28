package it.unibo.kactor.utils

import it.unibo.kactor.parameters.MutableParameterMap
import kotlinx.coroutines.CoroutineScope

object KnownParamNames {

    const val ANNOTATED_CLASS_NAMES = "ann_class_names"

    const val TRACE = "trace"
    const val MSG_LOGGING = "msgLogging"
    const val MQTT_IP = "mqttBrokerIP"
    const val MQTT_PORT = "mqttBrokerPort"
    const val MQTT_TOPIC = "mqttBrokerEventTopic"

    const val CTX_SCOPES = "ctxScopes"
    const val SYSTEM_SCOPE = "systemScope"
    const val ACTOR_SCOPE = "actor_scope_%"
    const val BLOCK_ANNOTATIONS = "blockAnnotations"

    const val CTX_IS_REMOTE = "isRemote"
    const val CTX_REMOTE_ACTOR_LIST = "remoteActors"

    const val START_TYPE = "startType"

    const val DENY_QACTOR_BY_CONSTRUCTION = "deny_qactor_by_constr"
    const val BLOCK_IO = "block_io"

}

fun MutableParameterMap.addBlockIOParam() : MutableParameterMap {
    this.addParam(KnownParamNames.BLOCK_IO, true)
    return this
}

fun MutableParameterMap.addAnnotatedClassesParams(vararg annotatedClass : Class<*>) : MutableParameterMap {
    this.addParam(KnownParamNames.ANNOTATED_CLASS_NAMES, annotatedClass.map { it.name })
    return this
}

fun MutableParameterMap.addSystemScope(scope : CoroutineScope) : MutableParameterMap {
    this.addParam(KnownParamNames.SYSTEM_SCOPE, scope)
    return this
}

/*
fun MutableParameterMap.addScopeForActor(name : String, scope: CoroutineScope) : MutableParameterMap {
    this.addParam(KnownParamNames.ACTOR_SCOPE.replace("%", name), scope)
    return this
}*/