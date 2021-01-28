@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.extension.org.lwjgl.memory

import io.github.itsusinn.extension.nio.putUInt
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

inline fun stack(
   noinline block:MemoryStack.() -> Unit
) = stackPush().use {
   block.invoke(it)
}


inline fun floatDirectArrayOf(
   vararg elements: Float,
):FloatBuffer = stackPush().use {
   it.mallocFloat(elements.size).put(elements).flip()
}

inline fun intDirectArrayOf(
   vararg elements: Int,
):IntBuffer = stackPush().use {
   it.mallocInt(elements.size).put(elements).flip()
}

inline fun uintDirectArrayOf(
   vararg elements: Int,
):IntBuffer{
   return BufferUtils.createIntBuffer(elements.size).put(elements).flip()
}