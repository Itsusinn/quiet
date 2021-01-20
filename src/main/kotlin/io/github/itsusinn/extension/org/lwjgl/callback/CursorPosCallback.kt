package io.github.itsusinn.extension.org.lwjgl.callback

import org.lwjgl.system.NativeType

interface CursorPosCallback {
   operator fun invoke(xpos: Double, ypos: Double)
}