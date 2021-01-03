package io.github.itsusinn.extension.org.lwjgl.event

data class KeyboardEvent(
   val key: Int,
   val scancode: Int,
   val action: Int,
   val mods: Int)