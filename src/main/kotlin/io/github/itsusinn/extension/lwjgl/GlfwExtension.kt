package io.github.itsusinn.extension.lwjgl

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import java.util.*
import kotlin.collections.ArrayList

//save reference of some objects to escape from the gc
private val GC_Root = ArrayList<Any>()
// a readable name for null pointer
const val NullPointer = 0L

inline val LwjglVersion: String
   get() = Version.getVersion()

/**
 * a short way to create a window
 *
 * @param width   the desired width, in screen coordinates, of the window
 * @param height  the desired height, in screen coordinates, of the window
 * @param title   initial, UTF-8 encoded window title
 * @param monitor the monitor to use for fullscreen mode, or Null for windowed mode
 * @param share   the window whose context to share resources with, or Null to not share resources
 *
 * @return the handle of the created window, or Null if an error occurred
 */
fun createWindow(
   width:Int = 900,
   height:Int = 1600,
   title:CharSequence = "GlfwWindow $LwjglVersion",
   monitor:Long = NullPointer,
   share:Long = NullPointer
): Window {
   // call original fun which returns handle to create window
   val handle = GLFW.glfwCreateWindow(width, height, title, monitor, share)
   // add a not null check here to make sure the non-null feat of kt
   if (handle == NullPointer) throw RuntimeException("Failed to create the GLFW window")
   return Window(handle)
}

object GlfwManager{

}

object GlfwConfiguration{
   fun init(){

   }
   fun setDefault(){
      GLFW.glfwDefaultWindowHints()
   }
}

/**
 * a abstract express of window handle
 */
class Window(val handle:Long){

   private val logicalMainThread = Thread.currentThread()

   /**
    * the value of the close flag of window.
    * can be called from any thread
    */
   var shouldClose:Boolean
      get() = GLFW.glfwWindowShouldClose(handle)
      set(value) = GLFW.glfwSetWindowShouldClose(handle,value)

   /**
    * Resets all callbacks for the GLFW window to Null
    * and Callback#free frees all previously set callbacks.
    */
   fun freeCallbacks() = Callbacks.glfwFreeCallbacks(handle)

   /**
    * Destroys the window and its context
    */
   fun destroy() = GLFW.glfwDestroyWindow(handle)

   /**
    * Sets the position, in screen coordinates, of the upper-left corner of the content area of the specified windowed mode window.
    * If the window is a full screen window, this function does nothing.
    */
   fun setWindowPos(xPos:Int, yPos:Int) = GLFW.glfwSetWindowPos(handle,xPos, yPos)

   /**
    * Sets the key callback of the window, which is called when a key is pressed, repeated or released.
    */
   fun setKeyCallback(keyCallback: KeyCallback) =
      GLFW.glfwSetKeyCallback(handle){ _, key, scancode, action, mods ->
         keyCallback.invoke(KeyContext(key, scancode, action, mods))
      }

}

fun setCurrentContext(window:Window) = GLFW.glfwMakeContextCurrent(window.handle)
fun clearCurrentContext() = GLFW.glfwMakeContextCurrent(NullPointer)

data class KeyContext(
   val key: Int,
   val scancode: Int,
   val action: Int,
   val mods: Int)

fun interface KeyCallback{
   fun invoke(keyContext: KeyContext)
}