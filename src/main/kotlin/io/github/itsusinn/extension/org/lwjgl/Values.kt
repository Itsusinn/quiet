package io.github.itsusinn.extension.org.lwjgl

import org.lwjgl.Version

// a readable name for null pointer
const val NullPointer = 0L
inline val LwjglVersion: String
   get() = Version.getVersion()