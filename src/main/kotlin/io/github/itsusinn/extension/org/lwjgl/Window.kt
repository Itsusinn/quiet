package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.java.thread.SingleThread
import io.github.itsusinn.extension.org.lwjgl.event.KeyboardEvent
import io.github.itsusinn.quiet.extension.org.lwjgl.unit.WindowSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

/**
 * a short way to create a window
 */
fun createWindow(
   identifier: String = "default",
   width:Int = 900,
   height:Int = 1600,
   title:CharSequence = "GlfwWindow $LwjglVersion",
   monitor:Long = NullPointer,
   share:Long = NullPointer
): Window {
   return Window(identifier, width, height, title, monitor, share)
}

/**
 * a abstract express of window handle
 *
 * @param width   the desired width, in screen coordinates, of the window
 * @param height  the desired height, in screen coordinates, of the window
 * @param title   initial, UTF-8 encoded window title
 * @param monitor the monitor to use for fullscreen mode, or Null for windowed mode
 * @param share   the window whose context to share resources with, or Null to not share resources
 *
 * @return the handle of the created window, or Null if an error occurred
 */
class Window(
   val identifier:String,
   val width:Int,
   val height:Int,
   val title:CharSequence,
   val monitor:Long,
   val share:Long,
   private val logicalMainThread: SingleThread = GlfwManager.logicalMainThread
   ): CoroutineScope {
   override val coroutineContext: CoroutineContext
      get() = logicalMainThread.coroutineContext

   var handle by Delegates.notNull<Long>()

   init {
      runBlocking {
         async {
            // Configure GLFW
            GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable

            // call original fun which returns handle to create window
            handle = GLFW.glfwCreateWindow(width, height, title, monitor, share)
            // add a not null check here to make sure the non-null feat of kt
            check(handle != NullPointer) { "Failed to create the GLFW window" }
         }.await()
      }
   }

   /**
    * the value of the close flag of window.
    * can be called from any thread
    */
   var shouldClose:Boolean
      get() = GLFW.glfwWindowShouldClose(handle)
      set(value) = GLFW.glfwSetWindowShouldClose(handle, value)

   /**
    * Resets all callbacks for the GLFW window to Null
    * and Callback#free frees all previously set callbacks.
    */
   fun freeCallbacks() = Callbacks.glfwFreeCallbacks(handle)

   /**
    * Destroys the window and its context
    */
   fun destroy() = async { GLFW.glfwDestroyWindow(handle) }

   /**
    * Sets the position, in screen coordinates, of the upper-left corner of the content area of the windowed mode window.
    * If the window is a full screen window, this function does nothing.
    */
   fun setWindowPos(xPos:Int, yPos:Int) = async { GLFW.glfwSetWindowPos(handle, xPos, yPos) }

   /**
    * Retrieves the size, in screen coordinates, of the content area of the specified window.
    */
   fun getWindowSize() = runBlocking<WindowSize>{
      async<WindowSize> {
         MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*
            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(handle, pWidth, pHeight)
            return@async WindowSize(pWidth[0],pHeight[0])
         }
      }.await()
   }

   /**
    * Sets the key callback of the window, which is called when a key is pressed, repeated or released.
    */
   fun setKeyboardCallback(keyboardCallback: (KeyboardEvent) -> Unit) = async {
      GLFW.glfwSetKeyCallback(handle) { _, key, scancode, action, mods ->
         keyboardCallback(KeyboardEvent(key, scancode, action, mods))
      }
   }

   /**
    * Makes the window visible if it was previously hidden.
    */
   fun show() = async { GLFW.glfwShowWindow(handle) }
}