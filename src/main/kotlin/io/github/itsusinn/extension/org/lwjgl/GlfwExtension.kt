package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.java.thread.SingleThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWVidMode
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

//save reference of some objects to escape from the gc
private val GC_Root = ArrayList<Any>()

object GlfwManager:CoroutineScope{
   val logicalMainThread = SingleThread.create("lwjgl")
   override val coroutineContext: CoroutineContext
      get() = logicalMainThread.coroutineContext

   /**
    * dynamic evaluation
    */
   val videoMode: GLFWVidMode
      get() = runBlocking<GLFWVidMode> {
         val videoMode = async {
            GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
         }.await()
         checkNotNull(videoMode)
         videoMode
      }

   /**
    * Destroys all remaining windows and cursors,
    * restores any modified gamma ramps and frees any other allocated resources.
    */
   fun terminate() = runBlocking<Unit> {
      async{ GLFW.glfwTerminate() }
      logicalMainThread.shutdown()
   }

   /**
    * Processes all pending events.
    * The key callback above will only be
    * invoked during this call.
    */
   fun pollEvents() = async { GLFW.glfwPollEvents() }
}
object GlfwConfiguration{
   fun init(){

   }
   fun setDefault(){
      GLFW.glfwDefaultWindowHints()
   }
}