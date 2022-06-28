package it.unibo.kactor

import it.unibo.kactor.annotations.AnnotationLoader
import it.unibo.kactor.utils.KnownParamNames
import it.unibo.kactor.builders.sysBuilder
import it.unibo.kactor.parameters.ReadableParameterMap
import it.unibo.kactor.parameters.asNameOf
import it.unibo.kactor.parameters.immutableParameterMapOf
import it.unibo.kactor.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

suspend fun launchQak(scope: CoroutineScope = GlobalScope) {
    launchQak(immutableParameterMapOf(KnownParamNames.SYSTEM_SCOPE asNameOf scope))
}

suspend fun launchQak(params : ReadableParameterMap) {
    println("       %%% qakLauncher | Launch with params [$params] ")
    val blockAnnotations = params.tryCastOrElse(KnownParamNames.BLOCK_ANNOTATIONS, false)
    println("       %%% qakLauncher | Checking annotations ")
    if(!blockAnnotations) {
        AnnotationLoader.loadSystemByAnnotations(params)
        println("       %%% qakLauncher | Annotation loading complete ")
    } else
        println("       %%% qakLauncher | Annotation loading is blocked ")


    println("       %%% qakLauncher | Building system... ")
    QakContext.createSystem(sysBuilder.build())
    println("       %%% qakLauncher | QakContext launch complete ")
}

suspend fun launchQak(vararg params : Pair<String, Any>) {
    launchQak(immutableParameterMapOf(*params))
}

fun launchQak(hostName: String, scope: CoroutineScope = GlobalScope ,
              desrFilePath: String, rulesFilePath: String) {
    QakContext.createContexts(hostName, scope, desrFilePath, rulesFilePath)
}

suspend fun lauchQakN() {
    println("               %%% qakLauncher | Checking annotations ")
    AnnotationLoader.loadSystemByAnnotations()
    println("               %%% qakLauncher | Annotation loading complete ")

    println("               %%% qakLauncher | Building system... ")
    QakContext.createSystem(sysBuilder.build())
    println("               %%% qakLauncher | QakContext launch complete ")
}