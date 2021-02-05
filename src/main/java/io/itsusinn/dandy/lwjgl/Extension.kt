package io.itsusinn.dandy.lwjgl

import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.withContext

inline fun <reified T, reified R, reified P> T.blocking(
    para: P,
    crossinline call: suspend (P) -> R
): R where T : CoroutineScope = runBlocking<R> {
    return@runBlocking withContext(coroutineContext) {
        call.invoke(para)
    }
}

inline fun <reified T, reified R, reified P1, reified P2> T.blocking(
    para1: P1,
    para2: P2,
    crossinline call: suspend (P1, P2) -> R
): R where T : CoroutineScope = runBlocking<R> {
    return@runBlocking withContext(coroutineContext) {
        call.invoke(para1, para2)
    }
}

inline fun <reified T, reified R> T.blocking(
    crossinline call: suspend () -> R
): R where T : CoroutineScope = runBlocking<R> {
    return@runBlocking withContext(coroutineContext) {
        call.invoke()
    }
}

public suspend inline fun <reified T> CoroutineScope.selfContext(
    noinline block: suspend CoroutineScope.() -> T
): T = withContext(coroutineContext, block)
