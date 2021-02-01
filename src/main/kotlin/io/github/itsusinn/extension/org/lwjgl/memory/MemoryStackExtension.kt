@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.extension.org.lwjgl.memory

import io.netty.buffer.PooledByteBufAllocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import java.nio.* // ktlint-disable no-wildcard-imports

val bufAllocator = PooledByteBufAllocator()

inline fun <reified R> stack(
    noinline block: MemoryStack.() -> R
): R = stackPush().use {
    block.invoke(it)
}
suspend inline fun <reified R> CoroutineScope.stack(
    noinline block: suspend MemoryStack.() -> R
): R = stackPush().use {
    return@use withContext(coroutineContext) {
        block(it)
    }
}

inline fun FloatArray.buf() = stack {
    floatDirectArrayOf(*this@buf)
}

inline fun floatDirectArrayOf(
    vararg elements: Float,
): FloatBuffer = stack {
    mallocFloat(elements.size).put(elements).flip()
}

inline fun IntArray.buf() = stack {
    intDirectArrayOf(*this@buf)
}

inline fun intDirectArrayOf(
    vararg elements: Int,
): IntBuffer = stack {
    mallocInt(elements.size).put(elements).flip()
}
