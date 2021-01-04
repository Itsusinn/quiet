package io.github.itsusinn.extension.org.lwjgl.event

import org.lwjgl.glfw.GLFW
import org.lwjgl.system.APIUtil
import org.lwjgl.system.MemoryUtil

data class KeyboardEvent(
   val key: Int,
   val scancode: Int,
   val action: Int,
   val mods: Int)

data class GLFWErrorEvent(
   val error: String,
   val description: String,
   val stack: Array<StackTraceElement>
){
   companion object Factory{
      fun create(errorCode:Int,description:Long): GLFWErrorEvent {
         return GLFWErrorEvent(
            getError(errorCode),
            MemoryUtil.memUTF8(description),
            Thread.currentThread().stackTrace
         )
      }
   }
}

private val ErrorCodeMapper =
   APIUtil.apiClassTokens(null, null, GLFW::class.java)
private fun getError(code:Int):String{
   if (0x10000 < code && code < 0x20000) return "Unknown Error"
   return ErrorCodeMapper[code] ?: "Unknown Error"
}