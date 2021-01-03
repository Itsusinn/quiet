package io.github.itsusinn.extension.org.lwjgl

import org.lwjgl.Version
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.APIUtil
import java.lang.reflect.Field
import java.util.function.BiPredicate

// a readable name for null pointer
const val NullPointer = 0L
inline val LwjglVersion: String
   get() = Version.getVersion()