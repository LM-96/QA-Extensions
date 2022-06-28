package it.unibo.kactor

import it.unibo.kactor.annotations.AnnotationLoader
import it.unibo.kactor.utils.KnownParamNames
import it.unibo.kactor.builders.sysBuilder
import it.unibo.kactor.parameters.*
import it.unibo.kactor.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex

private var QAK_LAUNCHED : Boolean = false
private val mutex = Mutex()

val LAUNCH_PARAMETERS = mutableParameterMap()
val ACTOR_SCOPE = lateSingleInit<CoroutineScope>()

private suspend fun tryLaunch(mutualExclusion : Boolean, launcher : suspend () -> Unit) {
    if(mutualExclusion) {
        mutex.lock()
    }
    try {
        if(!QAK_LAUNCHED) {
            launcher()
            QAK_LAUNCHED = true
        }
    } finally {
        if(mutualExclusion)
            mutex.unlock()
    }
}

suspend fun launchQak(scope: CoroutineScope = GlobalScope,
                      params : ReadableParameterMap = immutableParameterMap(),
                      mutualExclusion: Boolean = true
) =tryLaunch(mutualExclusion) {
    launchQak(params + (KnownParamNames.SYSTEM_SCOPE asNameOf scope), false)
    ACTOR_SCOPE.set(scope)
}

suspend fun launchQak(params : ReadableParameterMap, mutualExclusion: Boolean = true) = tryLaunch(mutualExclusion) {
    println("       %%% qakLauncher | Launch with params [$params] ")
    val blockAnnotations = params.tryCastOrElse(KnownParamNames.BLOCK_ANNOTATIONS, false)
    println("       %%% qakLauncher | Checking annotations ")
    if(!blockAnnotations) {
        AnnotationLoader.loadSystemByAnnotations(params)
        println("       %%% qakLauncher | Annotation loading complete ")
    } else
        println("       %%% qakLauncher | Annotation loading is blocked ")
    val blockIO = params.tryCastOrElse(KnownParamNames.BLOCK_IO, false)
    if(blockIO)
        sysUtil.ioEnabled = false

    println("       %%% qakLauncher | Building system... ")
    QakContext.createSystem(sysBuilder.build())
    println("       %%% qakLauncher | QakContext launch complete ")
}

suspend fun launchQak(vararg params : Pair<String, Any>,
                      mutualExclusion: Boolean = true
) = tryLaunch(mutualExclusion) {
    launchQak(immutableParameterMapOf(*params),false)
}

fun launchQak(hostName: String, scope: CoroutineScope = GlobalScope ,
              desrFilePath: String, rulesFilePath: String,
              mutualExclusion: Boolean = true
) = runBlocking {
    tryLaunch(mutualExclusion) {
        QakContext.createContexts(hostName, scope, desrFilePath, rulesFilePath)
    }
}

suspend fun lauchQakN(mutualExclusion: Boolean = true) = tryLaunch(mutualExclusion) {
        println("               %%% qakLauncher | Checking annotations ")
        AnnotationLoader.loadSystemByAnnotations()
        println("               %%% qakLauncher | Annotation loading complete ")

        println("               %%% qakLauncher | Building system... ")
        QakContext.createSystem(sysBuilder.build())
        println("               %%% qakLauncher | QakContext launch complete ")
}

fun qakActor(clazz : Class<*>, params: ReadableParameterMap = immutableParameterMap()) : IQActorBasic {
    runBlocking {
        launchQak(params)
    }
    return IQActorBasic.IQACTOR_ISTANCES[clazz]!!
}

fun qakActorFsm(clazz: Class<*>, params: ReadableParameterMap = immutableParameterMap()) : IQActorBasicFsm {
    runBlocking {
        launchQak(params)
    }
    return qakActor(clazz) as IQActorBasicFsm
}