package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.org.lwjgl.callback.CursorPosCallback
import io.github.itsusinn.extension.org.lwjgl.callback.KeyboardCallback
import io.github.itsusinn.quiet.extension.org.lwjgl.unit.WindowSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack
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
): GlfwWindow {
   return GlfwWindow(identifier, width, height, title, monitor, share)
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
class GlfwWindow(
   val identifier:String,
   val width:Int,
   val height:Int,
   val title:CharSequence,
   val monitor:Long,
   val share:Long,
   hintBuilder:Int = 0
   ): CoroutineScope by GlfwManager {
   private val dispatcher = coroutineContext
   var handle by Delegates.notNull<Long>()

   init {
      runBlocking {
         withContext(dispatcher){
            // Configure GLFW
            GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable

            // call original fun which returns handle to create window
            handle = GLFW.glfwCreateWindow(width, height, title, monitor, share)
            // add a not null check here to make sure the non-null feat of kt
            check(handle != NullPointer) { "Failed to create the GLFW window" }
         }
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
    * Destroys the window and its context
    */
   suspend fun destroy() = withContext(coroutineContext) {
      GLFW.glfwDestroyWindow(handle)
   }

   /**
    * Sets the position, in screen coordinates, of the upper-left corner of the content area of the windowed mode window.
    * If the window is a full screen window, this function does nothing.
    */
   suspend fun setWindowPos(xPos:Int, yPos:Int) = withContext(coroutineContext) {
      GLFW.glfwSetWindowPos(handle, xPos, yPos)
   }

   /**
    * Retrieves the size, in screen coordinates, of the content area of the specified window.
    */
   suspend fun getWindowSize():WindowSize = withContext(coroutineContext){
      MemoryStack.stackPush().use { stack ->
         val pWidth = stack.mallocInt(1) // int*
         val pHeight = stack.mallocInt(1) // int*
         // Get the window size passed to glfwCreateWindow
         GLFW.glfwGetWindowSize(handle, pWidth, pHeight)
         return@withContext WindowSize(pWidth[0],pHeight[0])
      }
   }

   /**
    * Sets the key callback of the window, which is called when a key is pressed, repeated or released.
    */
   suspend fun setKeyboardCallback(
      keyboardCallback: KeyboardCallback?
   ) = withContext(coroutineContext){
      if (keyboardCallback == null){
         GLFW.glfwSetKeyCallback(handle,null)
         return@withContext
      }
      GLFW.glfwSetKeyCallback(handle) cb@{
            handle:Long,
            key: Int,
            scancode: Int,
            action: Int,
            mods: Int ->
         if (handle!=this@GlfwWindow.handle) return@cb
         keyboardCallback.invoke(this@GlfwWindow,key, scancode, action, mods)
      }
   }
   suspend fun setCursorPosCallback(
      cursorPosCallback:CursorPosCallback?
   ) = withContext(coroutineContext){
      if (cursorPosCallback == null){
         GLFW.glfwSetCursorPosCallback(handle,null)
         return@withContext
      }
      GLFW.glfwSetCursorPosCallback(handle) cb@{
            handle:Long,
            xpos: Double,
            ypos: Double ->
         if (handle!=this@GlfwWindow.handle) return@cb
         cursorPosCallback.invoke(this@GlfwWindow,xpos,ypos)
      }
   }

   /**
    * Makes the window visible if it was previously hidden.
    */
   suspend fun show() = withContext(coroutineContext){
      GLFW.glfwShowWindow(handle)
   }
}

fun GlfwWindow.setAsCurrentContext() = GLFW.glfwMakeContextCurrent(handle)
fun GlfwWindow.releaseCurrentContext() = GLFW.glfwMakeContextCurrent(0)
fun GlfwWindow.swapBuffers() = GLFW.glfwSwapBuffers(handle)
/**
 * Resets all callbacks for the GLFW window to Null
 * and Callback#free frees all previously set callbacks.
 */
fun GlfwWindow.freeCallbacks() = Callbacks.glfwFreeCallbacks(handle)