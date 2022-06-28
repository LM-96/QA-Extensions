package it.unibo.kactor.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AtomicVar<T>(private var value : T) {

    private val mutex = Mutex()

    suspend fun set(value: T) {
        mutex.withLock {
            this.value = value
        }
    }

    suspend fun get() : T {
        return mutex.withLock {
            value
        }
    }

    suspend fun withValue(block : (T) -> Unit) {
        mutex.withLock {
            block(value)
        }
    }

    suspend fun <R> map(mapper : (T) -> R) : R {
        return mutex.withLock {
            mapper(value)
        }
    }

}

fun <T> T.asAtomic() : AtomicVar<T> {
    return AtomicVar(this)
}