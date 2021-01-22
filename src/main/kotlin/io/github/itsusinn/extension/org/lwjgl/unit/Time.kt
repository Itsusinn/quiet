@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.extension.org.lwjgl.unit

inline fun now() = ((System.nanoTime() - start) * 1E-9).toFloat()

val start = System.nanoTime()
