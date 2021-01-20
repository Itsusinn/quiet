package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.async.annotation.Blocking
import io.github.itsusinn.extension.async.annotation.NonBlocking
import io.github.itsusinn.extension.java.thread.SingleThread
import io.github.itsusinn.extension.org.lwjgl.event.GLFWErrorEvent
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWVidMode

object GlfwManager:CoroutineScope by SingleThreadCoroutineScope("lwjgl"){

   /**
    * Returns the current video mode of the specified monitor
    * dynamic evaluation
    */
   @Blocking
   val videoMode: GLFWVidMode
      get() = runBlocking<GLFWVidMode> {
         val videoMode = async {
            GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
         }.await()
         checkNotNull(videoMode)
         videoMode
      }

   /**
    * Initializes the GLFW library
    * @throws IllegalStateException if Unable to initialize GLFW
    */
   @Blocking
   fun init() = runBlocking {
      async {
         // Initialize GLFW. Most GLFW functions will not work before doing this.
         check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
      }
   }
   /**
    * Destroys all remaining windows and cursors,
    * restores any modified gamma ramps and frees any other allocated resources.
    */
   @Blocking
   fun terminate() = runBlocking<Unit> {
      async{
         GLFW.glfwTerminate()
         setErrorCallBack(null)
      }.await()
      cancel()
   }

   /**
    * Sets the error callback,
    * which is called with an error code and a human-readable description each time a GLFW error occurs.
    */
   @NonBlocking
   fun setErrorCallBack(cbfun:((GLFWErrorEvent) -> Unit)?) = async{
      if (cbfun == null){
         GLFW.glfwSetErrorCallback(null)!!.free()
         return@async
      }
      GLFW.glfwSetErrorCallback { errorCode:Int,description:Long ->
         cbfun(GLFWErrorEvent.create(errorCode, description))
      }!!.set()
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