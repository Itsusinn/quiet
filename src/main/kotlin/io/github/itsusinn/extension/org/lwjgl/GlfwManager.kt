package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.org.lwjgl.event.GLFWErrorEvent
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import kotlinx.coroutines.*
import org.lwjgl.glfw.GLFW
import kotlin.coroutines.CoroutineContext

object GlfwManager: CoroutineScope{
   private val thread = SingleThreadCoroutineScope("lwjgl")
   override val coroutineContext: CoroutineContext
      get() = thread.coroutineContext

   /**
    * Returns the current video mode of the specified monitor
    * dynamic evaluation
    */
   suspend fun getVideoMode() = withContext(coroutineContext){
      GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
   }


   /**
    * Initializes the GLFW library
    * @throws IllegalStateException if Unable to initialize GLFW
    */
   suspend fun init() = withContext(coroutineContext){
      // Initialize GLFW. Most GLFW functions will not work before doing this.
      check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
   }

   /**
    * Destroys all remaining windows and cursors,
    * restores any modified gamma ramps and frees any other allocated resources.
    */
   suspend fun terminate() = withContext(coroutineContext){
      GLFW.glfwTerminate()
      setErrorCallBack(null)
   }



   /**
    * Sets the error callback,
    * which is called with an error code and a human-readable description each time a GLFW error occurs.
    */
   suspend fun setErrorCallBack(
      callback:((GLFWErrorEvent) -> Unit)?
   ) = withContext(coroutineContext){
      if (callback == null){
         GLFW.glfwSetErrorCallback(null)?.free()
         return@withContext
      }
      GLFW.glfwSetErrorCallback { errorCode:Int,description:Long ->
         callback(GLFWErrorEvent.create(errorCode, description))
      }?.set()
   }


   /**
    * Processes all pending events.
    * The key callback above will only be
    * invoked during this call.
    */
   suspend fun pollEvents() = withContext(coroutineContext){
      GLFW.glfwPollEvents()
   }

}
object GlfwConfiguration{
   fun init(){

   }
   fun setDefault(){
      GLFW.glfwDefaultWindowHints()
   }
}