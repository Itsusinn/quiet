@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.extension.org.lwjgl.memory

import io.github.itsusinn.extension.nio.putUInt
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.PooledByteBufAllocator
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import java.nio.*

val bufAllocator = PooledByteBufAllocator()

inline fun <reified R> stack(
   noinline block:MemoryStack.() -> R
):R = stackPush().use {
   block.invoke(it)
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
):IntBuffer = stack {
   mallocInt(elements.size).put(elements).flip()
}