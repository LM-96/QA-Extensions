package it.unibo.kactor;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Test extends AutoQActorBasicFsm {


    @NotNull
    @Override
    public Function1<ActorBasicFsm, Unit> getBody() {
        return null;
    }

    @NotNull
    @Override
    public String getInitialState() {
        return null;
    }
}
