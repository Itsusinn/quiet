@file:Suppress("NOTHING_TO_INLINE")
package io.itsusinn.dandy.nio

import java.nio.BufferOverflowException
import java.nio.IntBuffer

inline fun IntBuffer.putUInt(vararg src: Int): IntBuffer {
    if (src.size > remaining()) throw BufferOverflowException()
    for (element in src) {
        val offset = element + Integer.MIN_VALUE
        put(offset)
    }
    return this
}
