package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.org.lwjgl.callback.CursorPosCallback
import io.github.itsusinn.extension.org.lwjgl.callback.KeyboardCallback
import io.github.itsusinn.extension.org.lwjgl.callback.MouseButtonCallback
import io.github.itsusinn.extension.org.lwjgl.callback.ScrollCallback
import io.github.itsusinn.quiet.extension.org.lwjgl.unit.WindowSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback
import org.lwjgl.glfw.GLFWScrollCallback
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
            glfwDefaultWindowHints() // optional, the current window hints are already the default
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

            // call original fun which returns handle to create window
            handle = glfwCreateWindow(width, height, title, monitor, share)
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
      get() = glfwWindowShouldClose(handle)
      set(value) = glfwSetWindowShouldClose(handle, value)

   /**
    * Destroys the window and its context
    */
   suspend fun destroy() = withContext(coroutineContext) {
      glfwDestroyWindow(handle)
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
         glfwGetWindowSize(handle, pWidth, pHeight)
         return@withContext WindowSize(pWidth[0],pHeight[0])
      }
   }

   /**
    * Sets the key callback of the window, which is called when a key is pressed, repeated or released.
    */
   suspend inline fun setKeyboardCallback(
      callback: KeyboardCallback
   ) = withContext(coroutineContext) {
      glfwSetKeyCallback(handle) cb@{
            handle:Long,
            key: Int,
            scancode: Int,
            action: Int,
            mods: Int ->
         if (handle!=this@GlfwWindow.handle) return@cb
         callback.invoke(this@GlfwWindow,key, scancode, action, mods)
      }
   }

   /**
    * Will be called when the cursor is moved.
    */
   suspend inline fun setCursorPosCallback(
      callback:CursorPosCallback
   ) = withContext(coroutineContext){
      glfwSetCursorPosCallback(handle) cb@{
            handle:Long,
            xpos: Double,
            ypos: Double ->
         if (handle!=this@GlfwWindow.handle) return@cb
         callback.invoke(this@GlfwWindow,xpos,ypos)
      }
   }

   suspend inline fun setMouseButtonCallback(
      callback:MouseButtonCallback
   ) = withContext(coroutineContext){
      glfwSetMouseButtonCallback(handle) cb@{
            handle: Long,
            button: Int,
            action: Int,
            mods: Int ->
         if (handle!=this@GlfwWindow.handle) return@cb
         callback.invoke(this@GlfwWindow, button, action, mods)
      }
   }

   suspend fun setScrollCallback(
      callback: ScrollCallback
   ) = withContext(coroutineContext){
      glfwSetScrollCallback(handle) cb@{
            handle: Long,
            xOffset: Double,
            yOffset: Double ->
         if (handle != this@GlfwWindow.handle) return@cb
         callback.invoke(this@GlfwWindow, xOffset, yOffset)
      }
   }

   /**
    * Makes the window visible if it was previously hidden.
    */
   suspend fun show() = withContext(coroutineContext){ glfwShowWindow(handle) }
}

fun GlfwWindow.setAsCurrentContext() = glfwMakeContextCurrent(handle)
fun GlfwWindow.releaseCurrentContext() = glfwMakeContextCurrent(0)
fun GlfwWindow.swapBuffers() = glfwSwapBuffers(handle)
/**
 * Resets all callbacks for the GLFW window to Null
 * and Callback#free frees all previously set callbacks.
 */
fun GlfwWindow.freeCallbacks() = Callbacks.glfwFreeCallbacks(handle)