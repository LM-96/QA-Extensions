package it.unibo.kactor.annotations

import io.github.classgraph.ClassGraph
import it.unibo.kactor.*
import it.unibo.kactor.builders.*
import it.unibo.kactor.model.TransientActorBasic
import it.unibo.kactor.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.lang.reflect.Method
import java.util.*

object AnnotationLoader {

    /*private fun hasToBeLoaded(classInfo: ClassInfo) : Boolean {
        return classInfo.hasAnnotation(Tracing::class.java) ||
                classInfo.hasAnnotation(Msglogging::class.java) ||
                classInfo.hasAnnotation(MqttBroker::class.java) ||
                classInfo.hasAnnotation(HostName::class.java) ||
                classInfo.hasAnnotation(QakContext::class.java) ||
                classInfo.hasAnnotation(QActor::class.java)
    }*/

    private fun loadByClassgraph() : Map<Class<out Annotation>, List<Class<*>>> {
        val res = mutableMapOf<Class<out Annotation>, MutableList<Class<*>>>()
        ClassGraph().enableClassInfo().enableAnnotationInfo().scan()
            .allClasses.forEach {
                if(it.hasAnnotation(Tracing::class.java))
                    res.addToList(Tracing::class.java, it.loadClass())

                if(it.hasAnnotation(Msglogging::class.java))
                    res.addToList(Msglogging::class.java, it.loadClass())

                if(it.hasAnnotation(MqttBroker::class.java))
                    res.addToList(MqttBroker::class.java, it.loadClass())

                if(it.hasAnnotation(HostName::class.java))
                    res.addToList(HostName::class.java, it.loadClass())

                if(it.hasAnnotation(QakContext::class.java))
                    res.addToList(QakContext::class.java, it.loadClass())

                if(it.hasAnnotation(QakContextList::class.java))
                    res.addToList(QakContextList::class.java, it.loadClass())

                if(it.hasAnnotation(QActor::class.java))
                    res.addToList(QActor::class.java, it.loadClass())
            }

        return res
    }

    private fun loadByNames(names : Collection<String>) : Map<Class<out Annotation>, List<Class<*>>> {
        val res = mutableMapOf<Class<out Annotation>, MutableList<Class<*>>>()
        names.forEach { name ->
            val clazz = Class.forName(name)
            if(clazz.isAnnotationPresent(Tracing::class.java))
                res.addToList(Tracing::class.java, clazz)

            if(clazz.isAnnotationPresent(Msglogging::class.java))
                res.addToList(Msglogging::class.java, clazz)

            if(clazz.isAnnotationPresent(MqttBroker::class.java))
                res.addToList(MqttBroker::class.java, clazz)

            if(clazz.isAnnotationPresent(HostName::class.java))
                res.addToList(HostName::class.java, clazz)

            if(clazz.isAnnotationPresent(QakContext::class.java))
                res.addToList(QakContext::class.java, clazz)

            if(clazz.isAnnotationPresent(QakContextList::class.java))
                res.addToList(QakContextList::class.java, clazz)

            if(clazz.isAnnotationPresent(QActor::class.java))
                res.addToList(QActor::class.java, clazz)
        }
        return res
    }

    private fun <K, V> MutableMap<K, MutableList<V>>.addToList(key : K, value : V) {
        if(!this.containsKey(key))
            this[key] = mutableListOf()
        this[key]!!.add(value)
    }

    private lateinit var classInfos : Map<Class<out Annotation>, List<Class<*>>>

    private val annotatedAsActor = mutableLoadableValue<Set<Class<*>>>()
    private val loadedActor = mutableMapOf<Class<*>, TransientActorBasic>()

    val loadedActorClassNames : Set<String>
        get() {
        return loadedActor.keys.map { it.name }.toSet()
    }

    val loadedActorNames : Set<String>
    get() {
        return loadedActor.map { it.value.actorName }.toSet()
    }

    private val traceOption = mutableLoadableValue<Boolean>()
    private val msgLoggingOption = mutableLoadableValue<Boolean>()
    private val mqttBroker = mutableLoadableValue<Triple<String, Int, String>>()
    private val contexts = mutableLoadableValue<List<QakContext>>()
    private val hostname = mutableLoadableValue<String>()

    fun getLoadedActor() : Map<Class<*>, TransientActorBasic> {
        return loadedActor
    }

    @Throws(LoadException::class, BuildException::class)
    fun loadSystemByAnnotations(params : ReadableParameterMap = immutableParameterMap()) : SystemBuilder {
        val names = params.getAs<List<String>>("ann_class_names")
        classInfos = if(names != null) {
            loadByNames(names)
        } else {
            loadByClassgraph()
        }

        println("          %%% annotationLoader | Loading system scanning annotations ")
        scanForTraceOption().ifLoaded {
            println("          %%% annotationLoader | Loaded trace option by annotation: $it")
            sysUtil.trace = it
            sysBuilder.addParameter(KnownParamNames.TRACE, it)
        }
        scanForMsgLoggingOption().ifLoaded {
            println("          %%% annotationLoader | Loaded msglogging option by annotation: $it")
            sysUtil.logMsgs = it
            sysBuilder.addParameter(KnownParamNames.MSG_LOGGING, it)
        }
        mqttBroker.ifLoaded {
            println("          %%% annotationLoader | Loaded mqtt broker by annotation: $it")
            sysBuilder.addParameter(KnownParamNames.MQTT_IP, it.first)
            sysBuilder.addParameter(KnownParamNames.MQTT_PORT,it.second)
            sysBuilder.addParameter(KnownParamNames.MQTT_TOPIC, it.third)
        }

        scanForHostname().ifLoaded {
            println("          %%% annotationLoader | Loaded hostname by annotation: $it")
            sysBuilder.addHostname(it)
        }


        params.ifNotPresent(KnownParamNames.SYSTEM_SCOPE) {
            println("          %%% annotationLoader | WARNING: System scope not specified as parameter of \'loadSystemByAnnotations\' method. Will be used the \'${GlobalScope::class.java.simpleName}\' scope")
        }.ifNotPresent(KnownParamNames.CTX_SCOPES) {
            println("          %%% annotationLoader | WARNING: Context scopes not specified as parameter of \'loadSystemByAnnotations\' method. Will be used the system scope")
        }.ifIsNotTypeOf(KnownParamNames.SYSTEM_SCOPE, CoroutineScope::class.java) {
            println("          %%% annotationLoader | WARNING: System scope has the invalid type \'${it.javaClass.name}\'. Will be used the \'${GlobalScope::class.java.simpleName}\' scope")
        }.ifIsNotTypeOf(KnownParamNames.CTX_SCOPES, Map::class.java) {
            println("          %%% annotationLoader | WARNING: Context scopes has the invalid type '${it.javaClass.name}'. Will be used the system scope")
        }

        val contexts = scanForContexts().aGet()

        val actors = scanForActorClasses().aGet().groupBy { it.getAnnotation(QActor::class.java).contextName }
        val ctxScopes = params.tryCastOrElse(KnownParamNames.CTX_SCOPES, mutableMapOf<String, CoroutineScope>())
        val systemScope = params.tryCastOrElse(KnownParamNames.SYSTEM_SCOPE, GlobalScope)

        for(ctx in contexts) {
            println("          %%% annotationLoader | Loading context ${ctx.contextName}")
            val ctxBuilder = sysBuilder.newContext().addByContextAnnotation(ctx)
            if(ctxScopes.containsKey(ctx.contextName))
                ctxBuilder.addContextScope(ctxScopes[ctx.contextName]!!)
            else
                ctxBuilder.addContextScope(systemScope)

            val actorsInCtx = actors[ctx.contextName]
            if(actorsInCtx != null)
                for(actor in actorsInCtx) {
                    println("          %%% annotationLoader | Loading actor represented by class ${actor.name}")
                    loadActorByClass(actor, ctxBuilder)
                }

            ctxBuilder.buildInSystem()
        }
        println("          %%% annotationLoader | Load system scanning annotations completed")

        return sysBuilder
    }

    fun scanForActorClasses() : LoadResult<Set<Class<*>>> {
        return annotatedAsActor.ifMutableUntouched {
            load {
                classInfos[QActor::class.java]?.toSet() ?: mutableSetOf()
            }
        }.asLoadResult()
    }

    @Throws(LoadException::class)
    fun scanForTraceOption() : LoadResult<Boolean> {
        return traceOption.ifMutableUntouched {
            val annotated = classInfos[Tracing::class.java]
            if(annotated?.isEmpty() == false) {
                if (annotated.size != 1)
                    throw LoadException("Multiple @${Tracing::class.java.simpleName} annotation not allowed")
                load {
                    (annotated[0].getAnnotation(Tracing::class.java)).active
                }
            } else
                notFound()

        }.asLoadResult()
    }

    @Throws(LoadException::class)
    fun scanForMsgLoggingOption() : LoadResult<Boolean> {
        return msgLoggingOption.ifMutableUntouched {
            val annotated = classInfos[Msglogging::class.java]
            if(annotated?.isEmpty() == false) {
                if (annotated.size != 1)
                    throw LoadException("Multiple @${Msglogging::class.java.simpleName} annotation not allowed")
                load {
                    (annotated[0].getAnnotation(Msglogging::class.java)).active
                }
            } else
                notFound()

        }.asLoadResult()
    }

    fun scanForMqqtBroker() : LoadResult<Triple<String, Int, String>> {
        return mqttBroker.ifMutableUntouched {
            val annotated = classInfos[MqttBroker::class.java]
            if(annotated?.isEmpty() == false) {
                if(annotated.size != 1)
                    throw IllegalStateException("Multiple @${MqttBroker::class.java.simpleName} annotation not allowed")
                val ann = annotated[0].getAnnotation(MqttBroker::class.java)
                load {  Triple(ann.address, ann.port, ann.topic)
               }
            } else
                notFound()

        }.asLoadResult()
    }

    fun scanForContexts() : LoadResult<List<QakContext>> {
        return contexts.ifMutableUntouched {
            load {
                val ctx = classInfos[QakContext::class.java]
                    ?.map { it.getAnnotation(QakContext::class.java) } ?: mutableListOf()
                val ctxs = classInfos[QakContextList::class.java]
                    ?.map { it.getAnnotation(QakContextList::class.java).contexts.asList() }
                    ?.flatten() ?: mutableListOf()

                ctx.plus(ctxs)
            }
        }.asLoadResult()
    }

    fun scanForActorClassesWithSpecificContext(ctxName: String) : List<Class<*>>{
        annotatedAsActor.ifMutableUntouched { scanForActorClasses() }
        return annotatedAsActor.aMap { actors ->
            actors!!.filter { a -> a.getAnnotation(QActor::class.java).contextName == ctxName }
        }

    }

    @Throws(LoadException::class)
    fun scanForHostname() : LoadResult<String> {
        return hostname.ifMutableUntouched {
            val annotated = classInfos[HostName::class.java]
            if(annotated != null) {
                if(annotated.size > 1)
                    throw LoadException("Only ONE AND ONLY ONE @${HostName::class.java.simpleName} allowed. " +
                            "Please remove some of these annotations")
                else
                    load {
                        annotated.first().getAnnotation(HostName::class.java).hostname
                    }
            }
            else
                notFound()

        }.asLoadResult()
    }

    fun loadAllActors() : List<ActorBasic> {
        return annotatedAsActor.ifMutableUntouched {
            scanForActorClasses()
        }.pMap {
            actors -> actors.map { a -> loadActorByClass(a).wrap() }
        }
    }

    @Throws(LoadException::class)
    fun loadActorByClass(clazz : Class<*>, contextBuilder: ContextBuilder? = null) : TransientActorBasic {
        sysUtil.traceprintln("\t#loadByActorClass($clazz, $contextBuilder): invoked")
        if(loadedActor.containsKey(clazz)) {
            sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): actor aleady loaded")
            return loadedActor[clazz]!!
        }

        val actorAnnotation = clazz.getAnnotation(QActor::class.java)
            ?: throw LoadException("Class ${clazz.name} has no actor annotation: unable to load an actor")
        sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): found annotation [$actorAnnotation]")

        val actorName = getActorName(actorAnnotation, clazz).lowercase(Locale.getDefault())
        sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): found actor name [$actorName]")
        val actorBuilder = contextBuilder?.newActorBasic() ?: ActorBasicFsmBuilder()
        actorBuilder.addActorName(actorName)
            .addDiscardMessageOption(actorAnnotation.discardMessage).addConfinedOption(actorAnnotation.confined)
            .addIoBoundOption(actorAnnotation.ioBound).addChannelSizeOption(actorAnnotation.channelSize)

        var actorBuilderFsm : ActorBasicFsmBuilder? = null
        val type = clazz.getActorClassType()
        sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): found actor class type [$type]")
        when(type) {
            ActorClassType.ACTOR_BASIC_ONLY, ActorClassType.ACTOR_BASIC_FSM -> {
                actorBuilder.addActorBasicClassBody(clazz as Class<out ActorBasic>)
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): added ActorBasicClassBody")
            }


            ActorClassType.AUTO_QACTOR_BASIC_ONLY -> {
                actorBuilder.addAutoActorBody(clazz as Class<out AutoQActorBasic>)
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): added AutoActorBody")
            }

            ActorClassType.AUTO_QACTOR_BASIC_FMS -> {
                actorBuilderFsm = actorBuilder.upgrateToFsmBuilder()
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): upgraded to FSM builder [$actorBuilderFsm]")
                actorBuilderFsm.addAutoActorFsmBody(clazz as Class<out AutoQActorBasicFsm>)
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): added AutoActorFsmBody")
            }

            ActorClassType.QACTOR_BASIC_ONLY -> {
                loadByQActorBasicClass(clazz as Class<out QActorBasic>, actorBuilder)
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): calling \'loadByQActorBasicClass\'")
            }

            ActorClassType.QACTOR_BASIC_FSM -> {
                actorBuilderFsm = actorBuilder.upgrateToFsmBuilder()
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): upgraded to FSM builder [$actorBuilderFsm]")
                loadByQActorBasicFsmClass(clazz, actorName, actorBuilderFsm)
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): calling \'loadByQActorBasicFsmClass\'")

            }

            else -> {
                sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): invalid class type")
                throw LoadException("Invalid class annotated with @${QActor::class.java.simpleName}")
            }
        }

        try {
            val actor =
                if(contextBuilder != null) {
                    actorBuilderFsm?.buildInContext()?.first ?: actorBuilder.buildInContext().first
                } else
                    actorBuilderFsm?.build() ?: actorBuilder.build()
            sysUtil.traceprintln("\t#loadByActorClass(${clazz.simpleName}, ...): " +
                    "created actor instance \'${actor.actorName}\' [$actor]")
            loadedActor[clazz] = actor
            return actor
        } catch (e : Exception) {
            throw LoadException("Error building actor [class=${clazz.name}]", e)
        }
    }

    private fun getActorName(aAnn : QActor, clazz: Class<*>) : String {
        sysUtil.traceprintln("\t#getActorName($aAnn, $clazz): invoked")
        return if(aAnn.actorName.trim() != "") {
            sysUtil.traceprintln("\t#getActorName(${aAnn.actorName}, ...): name found by annotation")
            aAnn.actorName
        }
        else {
            sysUtil.traceprintln("\t#getActorName(${aAnn.actorName}, ...): name generated by class name")
            clazz.simpleName
        }
    }

    private fun loadByQActorBasicClass(clazz : Class<out QActorBasic>, actorBuilder: ActorBasicBuilder) {
        sysUtil.traceprintln("\t#loadByQActorBasicClass($clazz, $actorBuilder): invoked")
        var method : Method? = null
        for(m in clazz.declaredMethods){
            sysUtil.traceprintln("\t#loadByQActorBasicClass(${clazz.simpleName}, ...): scanning method \'${m.name}\' [$m]")
            if(m.isAnnotationPresent(ActorBody::class.java)) {
                sysUtil.traceprintln("\t#loadByQActorBasicClass(${clazz.simpleName}, ...): found annotation @${ActorBody::class.java}")
                if(method != null) {
                    sysUtil.traceprintln("\t#loadByQActorBasicClass(${clazz.simpleName}, ...): error: unable to have two bodies")
                    throw LoadException("Duplicate annotation ${ActorBody::class.java.simpleName} inside class ${clazz.name} not allowed")
                }
                method = m
            }
        }
        if(method == null) {
            sysUtil.traceprintln("\t#loadByQActorBasicClass(${clazz.simpleName}, ...): no body method found")
            throw LoadException("Class ${clazz.name} does not have a method annotated with ${ActorBody::class.java.simpleName}")
        }
        actorBuilder.addQActorMethodBody(method, clazz.getConstructor().newInstance())
        sysUtil.traceprintln("\t#loadByQActorBasicClass(${clazz.simpleName}, ...): added QActorMethodBody to builder")
    }

    private fun loadByQActorBasicFsmClass(
        clazz : Class<*>, actorName : String, actorBuilder: ActorBasicFsmBuilder,
        qActorBasicFsmInstance : QActorBasicFsm? = null
    ) {
        sysUtil.traceprintln("\t#loadByQActorBasicFsmClass($clazz, $actorBuilder): invoked")
        val qakactor = qActorBasicFsmInstance ?: clazz.getConstructor().newInstance() as QActorBasicFsm
        sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): created \'QActorBasicFsm\' instance [$qakactor]")
        actorBuilder.addQActorBasicFsm(qakactor)
        sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): added 'QActorBasicFsm' instance to builder")

        val stateMethods = clazz.declaredMethods
            .filter { it.isAnnotationPresent(`it.unibo`.kactor.annotations.State::class.java) }
            .associateBy {
                val ann = it.getAnnotation(`it.unibo`.kactor.annotations.State::class.java)
                if(ann.name != "") ann.name else it.name
            }
        sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): found ${stateMethods.size} state methods")

        val guardMethods = clazz.declaredMethods
            .filter { it.isAnnotationPresent(GuardFor::class.java) }
            .associateBy { it.getAnnotation(GuardFor::class.java)!!.transitionEdgeName }
        sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): found ${guardMethods.size} guards")

        for(g in guardMethods.values) {
            if (g.returnType != Boolean::class.java)
                throw LoadException(
                    "Method ${g.name} declared by ${g.declaringClass.name} is " +
                            "annotanotated with @${it.unibo.kactor.annotations.GuardFor::class.simpleName}: the " +
                            "return type MUST be a Boolean"
                )
            sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): checked guard method \'${g.name}\' [$g]")
        }

        if(stateMethods.keys.distinct().size < stateMethods.keys.size)
            throw LoadException("Duplicated states in [actor='$actorName', class='${clazz.name}']")


        for(s in stateMethods) {
            sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): building state \'${s.key}\' by method \'${s.value.name}\'")
            buildState(qakactor, actorBuilder, s.key, s.value, guardMethods)
            sysUtil.traceprintln("\t#loadByQActorBasicFsmClass(${clazz.simpleName}, ...): state \'${s.key}\' built")
        }
    }

    @Throws(LoadException::class)
    private fun buildState(qactor : QActorBasic, actorBuilder : ActorBasicFsmBuilder, stateName : String,
                           method : Method, guards : Map<String, Method>) {
        sysUtil.traceprintln("\t#buildState($qactor, $actorBuilder, $stateName, $method, $guards): invoked")
        if(method.hasParameters())
            throw LoadException("Method ${method.name} declared by ${method.declaringClass.name} is " +
                    "annotanotated with @${State::class.simpleName}: no " +
                    "input parameters allowed")
        val stateBuilder = actorBuilder.newState()
            .addStateName(stateName).addStateBodyByQActorMethod(method)
        sysUtil.traceprintln("\t#buildState($stateName,...): added StateBodyByQActorMethod [$method]")

        if(method.isAnnotationPresent(Initial::class.java)) {
            actorBuilder.setInitialState(stateName)
            sysUtil.traceprintln("\t#buildState($stateName,...): state \'$stateName\' is initial")
        }

        for(ann in method.getAnnotationsByType(EpsilonMove::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected epsilon move annotation")
            parseEpsilonMove(ann, stateBuilder, guards, qactor)
        }

        for(ann in method.getAnnotationsByType(WhenDispatch::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when dispatch annotation")
            parseWhenDispatch(ann, stateBuilder, guards, qactor)
        }

        for(ann in method.getAnnotationsByType(WhenRequest::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when dispatch annotation")
            parseWhenRequest(ann, stateBuilder, guards, qactor)
        }

        for(ann in method.getAnnotationsByType(WhenReply::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when reply annotation")
            parseWhenReply(ann, stateBuilder, guards, qactor)
        }

        for(ann in method.getAnnotationsByType(WhenEvent::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when event annotation")
            parseWhenEvent(ann, stateBuilder, guards, qactor)
        }

        /*if(method.isAnnotationPresent(EpsilonMove::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected epsilon move annotation")
            parseEpsilonMove(method.getAnnotation(EpsilonMove::class.java), stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenDispatch::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when dispatch annotation")
            parseWhenDispatch(method.getAnnotation(WhenDispatch::class.java), stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenRequest::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when dispatch annotation")
            parseWhenRequest( method.getAnnotation(WhenRequest::class.java), stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenReply::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when reply annotation")
            parseWhenReply(method.getAnnotation(WhenReply::class.java), stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenEvent::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when event annotation")
            parseWhenEvent(method.getAnnotation(WhenEvent::class.java), stateBuilder, guards, qactor)
        }*/

        if(method.isAnnotationPresent(WhenTime::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when time annotation")
            parseWhenTime(method.getAnnotation(WhenTime::class.java), stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenDispatches::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when dispatched annotation")
            for(tAnn in method.getAnnotation(WhenDispatches::class.java).whenDispatches)
                parseWhenDispatch(tAnn, stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenRequests::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when requests annotation")
            for(tAnn in method.getAnnotation(WhenRequests::class.java).whenRequests)
                parseWhenRequest(tAnn, stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenReplies::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when replies annotation")
            for(tAnn in method.getAnnotation(WhenReplies::class.java).whenReplies)
                parseWhenReply(tAnn, stateBuilder, guards, qactor)
        }

        if(method.isAnnotationPresent(WhenEvents::class.java)) {
            sysUtil.traceprintln("\t#buildState($stateName,...): detected when events annotation")
            for(tAnn in method.getAnnotation(WhenEvents::class.java).whenEvents)
                parseWhenEvent(tAnn, stateBuilder, guards, qactor)
        }

        sysUtil.traceprintln("\t#buildState($stateName,...): building state \'$stateName\'")
        stateBuilder.buildState()
        sysUtil.traceprintln("\t#buildState($stateName,...): state \'$stateName\' builded")
    }

    private fun parseEpsilonMove(tAnn : EpsilonMove, stateBuilder: StateBuilder, guards : Map<String, Method>,
                                 qactor : QActorBasic
    ) {
        sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, $stateBuilder, $guards, $qactor): invoked")

        val transitionBuilder = stateBuilder.newTransition()
        sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): creating new transition")

        transitionBuilder.addEdgeName(tAnn.edgeName).addTargetState(tAnn.targetState)
        sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): added edge name [${tAnn.edgeName}]")
        sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): added target state [${tAnn.targetState}]")

        val guard = guards[tAnn.edgeName]
        sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): scanning guard for \'${tAnn.edgeName}\'")
        if(guard != null) {
            sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): found a guard [${tAnn.edgeName}]")
            transitionBuilder.buildEpsilonMoveGuarded{ guard.invoke(qactor) as Boolean }
            sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): builded epsilon move with guard [${tAnn.edgeName}]")
            val elseTarget = guard.getAnnotation(GuardFor::class.java).elseTarget
            if( elseTarget!= "") {
                sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): found else target for guard [${tAnn.edgeName}]")
                transitionBuilder.clear().addEdgeName("$#else#_#for#_${tAnn.edgeName}]")
                    .addTargetState(elseTarget)
                    .buildEpsilonMoveGuarded { !(guard.invoke(qactor) as Boolean) }
                sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): builded epsilon move for else target [${tAnn.edgeName}]")
            }
        } else {
            sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): no guard found [${tAnn.edgeName}]")
            transitionBuilder.buildEpsilonMove()
            sysUtil.traceprintln("\t#parseEpsilonMove($tAnn, ...): builded epsilon move [${tAnn.edgeName}]")
        }
    }

    private fun parseWhenDispatch(tAnn : WhenDispatch, stateBuilder: StateBuilder, guards : Map<String, Method>,
                                  qactor : QActorBasic
    ) {

        sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, $stateBuilder, $guards, $qactor): invoked")

        val transitionBuilder = stateBuilder.newTransition()
        sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): creating new transition")

        transitionBuilder.addEdgeName(tAnn.edgeName).addTargetState(tAnn.targetState)
        sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): added edge name [${tAnn.edgeName}]")
        sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): added target state [${tAnn.targetState}]")

        val guard = guards[tAnn.edgeName]
        sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): scanning guard for \'${tAnn.edgeName}\'")

        if(guard != null) {

            sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): found a guard [${tAnn.edgeName}]")
            transitionBuilder.buildWhenDispatchGuarded(tAnn.messageName){ guard.invoke(qactor) as Boolean }
            sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): builded when dispatch transition with guard [${tAnn.edgeName}]")

            val elseTarget = guard.getAnnotation(GuardFor::class.java).elseTarget
            if( elseTarget!= "") {
                sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): found else target for guard [${tAnn.edgeName}]")
                transitionBuilder.clear().addEdgeName("$#else#_#for#_${tAnn.edgeName}")
                    .addTargetState(elseTarget)
                    .buildWhenDispatchGuarded(tAnn.messageName) { !(guard.invoke(qactor) as Boolean) }
                sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, $stateBuilder, $guards, $qactor): builded when dispatch transition for else target [${tAnn.edgeName}]")

            }
        } else {
            sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): no guard found [${tAnn.edgeName}]")
            transitionBuilder.buildWhenDispatch(tAnn.messageName)
            sysUtil.traceprintln("\t#parseWhenDispatch($tAnn, ...): builded when dispatch transition [${tAnn.edgeName}]")
        }
    }

    private fun parseWhenRequest(tAnn : WhenRequest, stateBuilder: StateBuilder, guards : Map<String, Method>,
                                 qactor : QActorBasic
    ) {
        sysUtil.traceprintln("\t#parseWhenRequest($tAnn, $stateBuilder, $guards, $qactor): invoked")

        val transitionBuilder = stateBuilder.newTransition()
        sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): creating new transition")

        transitionBuilder.addEdgeName(tAnn.edgeName).addTargetState(tAnn.targetState)
        sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): added edge name [${tAnn.edgeName}]")
        sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): added target state [${tAnn.targetState}]")

        val guard = guards[tAnn.edgeName]
        sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): scanning guard for \'${tAnn.edgeName}\'")
        if(guard != null) {

            sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): found a guard [${tAnn.edgeName}]")
            transitionBuilder.buildWhenRequestGuarded(tAnn.messageName){ guard.invoke(qactor) as Boolean }
            sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): builded when request transition with guard [${tAnn.edgeName}]")

            val elseTarget = guard.getAnnotation(GuardFor::class.java).elseTarget
            if( elseTarget!= "") {

                sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): found else target for guard [${tAnn.edgeName}]")
                transitionBuilder.clear().addEdgeName("$#else#_#for#_${tAnn.edgeName}")
                    .addTargetState(elseTarget)
                    .buildWhenRequestGuarded(tAnn.messageName) { !(guard.invoke(qactor) as Boolean) }
                sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): builded when request transition for else target [${tAnn.edgeName}]")
            }
        } else {
            sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): no guard found [${tAnn.edgeName}")
            transitionBuilder.buildWhenRequest(tAnn.messageName)
            sysUtil.traceprintln("\t#parseWhenRequest($tAnn, ...): builded when request transition [${tAnn.edgeName}]")

        }
    }

    private fun parseWhenReply(tAnn : WhenReply, stateBuilder: StateBuilder, guards : Map<String, Method>,
                               qactor : QActorBasic
    ) {
        sysUtil.traceprintln("\t#parseWhenReply($tAnn, $stateBuilder, $guards, $qactor): invoked")
        val transitionBuilder = stateBuilder.newTransition()
        sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): creating new transition")

        transitionBuilder.addEdgeName(tAnn.edgeName).addTargetState(tAnn.targetState)
        sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): added edge name [${tAnn.edgeName}]")
        sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): added target state [${tAnn.targetState}]")

        val guard = guards[tAnn.edgeName]
        sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): scanning guard for \'${tAnn.edgeName}\'")

        if(guard != null) {

            sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): found a guard [${tAnn.edgeName}")
            transitionBuilder.buildWhenReplyGuarded(tAnn.messageName){ guard.invoke(qactor) as Boolean }
            sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): builded when reply transition with guard [${tAnn.edgeName}]")

            val elseTarget = guard.getAnnotation(GuardFor::class.java).elseTarget
            if( elseTarget!= "") {

                sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): found else target for guard [${tAnn.edgeName}]")
                transitionBuilder.clear().addEdgeName("$#else#_#for#_${tAnn.edgeName}")
                    .addTargetState(elseTarget)
                    .buildWhenReplyGuarded(tAnn.messageName) { !(guard.invoke(qactor) as Boolean) }
                sysUtil.traceprintln("\t#parseWhenReply($tAnn,...): builded when reply transition for else target [${tAnn.edgeName}]")

            }
        } else {
            sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): no guard found [${tAnn.edgeName}]")
            transitionBuilder.buildWhenReply(tAnn.messageName)
            sysUtil.traceprintln("\t#parseWhenReply($tAnn, ...): builded when reply transition [${tAnn.edgeName}]")

        }
    }

    private fun parseWhenEvent(tAnn : WhenEvent, stateBuilder: StateBuilder, guards : Map<String, Method>,
                               qactor : QActorBasic
    ) {
        sysUtil.traceprintln("\t#parseWhenEvent($tAnn, $stateBuilder, $guards, $qactor): invoked")
        val transitionBuilder = stateBuilder.newTransition()
        sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): creating new transition")

        transitionBuilder.addEdgeName(tAnn.edgeName).addTargetState(tAnn.targetState)
        sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): added edge name [${tAnn.edgeName}]")
        sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): added target state [${tAnn.targetState}]")

        val guard = guards[tAnn.edgeName]
        sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): scanning guard for \'${tAnn.edgeName}\'")

        if(guard != null) {
            sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): found a guard [${tAnn.edgeName}]")
            transitionBuilder.buildWhenEventGuarded(tAnn.eventName){ guard.invoke(qactor) as Boolean }
            sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): builded when event transition with guard [${tAnn.edgeName}]")

            val elseTarget = guard.getAnnotation(GuardFor::class.java).elseTarget
            if( elseTarget!= "") {
                sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): found else target for guard [${tAnn.edgeName}]")
                transitionBuilder.clear().addEdgeName("$#else#_#for#_${tAnn.edgeName}")
                    .addTargetState(elseTarget)
                    .buildWhenEventGuarded(tAnn.eventName) { !(guard.invoke(qactor) as Boolean) }
                sysUtil.traceprintln("\t#parseWhenEvent($tAnn,...): builded when event transition for else target [${tAnn.edgeName}]")
            }
        } else {
            sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): no guard found [${tAnn.edgeName}")
            transitionBuilder.buildWhenEvent(tAnn.eventName)
            sysUtil.traceprintln("\t#parseWhenEvent($tAnn, ...): builded when event transition [${tAnn.edgeName}]")
        }
    }

    private fun parseWhenTime(tAnn : WhenTime, stateBuilder: StateBuilder, guards : Map<String, Method>,
                                 qactor : QActorBasic
    ) {
        sysUtil.traceprintln("\t#parseWhenTime($tAnn, $stateBuilder, $guards, $qactor): invoked")
        val transitionBuilder = stateBuilder.newTransition()
        sysUtil.traceprintln("\t#parseWhenTime($tAnn, ...): creating new transition")

        transitionBuilder.addEdgeName(tAnn.edgeName).addTargetState(tAnn.targetState)
        sysUtil.traceprintln("\t#parseWhenTime($tAnn, ...): added edge name [${tAnn.edgeName}]")
        sysUtil.traceprintln("\t#parseWhenTime($tAnn, ...): added target state [${tAnn.targetState}]")

        transitionBuilder.buildWhenTimeout(tAnn.millis)
        sysUtil.traceprintln("\t#parseWhenTime($tAnn,...): builded when time transition for else target [${tAnn.edgeName}]")

    }
}