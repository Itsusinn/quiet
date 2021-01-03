package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.LwjglVersion
import io.github.itsusinn.extension.org.lwjgl.Window
import io.github.itsusinn.extension.org.lwjgl.createWindow
import io.github.itsusinn.extension.org.lwjgl.setCurrentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.lwjgl.glfw.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import kotlin.coroutines.CoroutineContext

class HelloWorld:CoroutineScope {
   lateinit var window: Window
   // The window handle
   private val windowHandle: Long by lazy { window.handle }

   override val coroutineContext: CoroutineContext
      get() = window.coroutineContext

   fun run() = async{
      println("Hello LWJGL $LwjglVersion!")
      init()

      //loop() is a blocking method
      loop()

      // Free the window callbacks and destroy the window
      window.freeCallbacks()
      window.destroy()

      // Terminate GLFW and free the error callback
      GLFW.glfwTerminate()
      GLFW.glfwSetErrorCallback(null)!!.free()
   }

   private fun init() {
      // Setup an error callback. The default implementation
      // will print the error message in System.err.
      GLFWErrorCallback.createPrint(System.err).set()

      // Create the window
      window = createWindow("ad",300, 300, "Hello World!")

      // Setup a key callback. It will be called every time a key is pressed, repeated or released.
      window.setKeyboardCallback {
         if (it.key == GLFW.GLFW_KEY_ESCAPE && it.action == GLFW.GLFW_RELEASE) {
            window.shouldClose = true
         }
         // We will detect this in the rendering loop
      }

      MemoryStack.stackPush().use { stack ->
         val pWidth = stack.mallocInt(1) // int*
         val pHeight = stack.mallocInt(1) // int*

         // Get the window size passed to glfwCreateWindow
         GLFW.glfwGetWindowSize(windowHandle, pWidth, pHeight)

         // Get the resolution of the primary monitor
         val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
         // Center the window
         window.setWindowPos((videoMode!!.width() - pWidth[0]) / 2, (videoMode.height() - pHeight[0]) / 2)

      }

      // Make the OpenGL context current
      setCurrentContext(window)
      // Enable v-sync
      GLFW.glfwSwapInterval(1)
      // Make the window visible
      GLFW.glfwShowWindow(windowHandle)
   }

   private fun loop() = async{
      // This line is critical for LWJGL's interoperation with GLFW's
      // OpenGL context, or any context that is managed externally.
      // LWJGL detects the context that is current in the current thread,
      // creates the GLCapabilities instance and makes the OpenGL
      // bindings available for use.
      GL.createCapabilities()

      // Set the clear color
      GL11.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

      // Run the rendering loop until the user has attempted to close
      // the window or has pressed the ESCAPE key.
      while (!window.shouldClose) {
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT) // clear the framebuffer
         GLFW.glfwSwapBuffers(windowHandle) // swap the color buffers

         // Poll for window events. The key callback above will only be
         // invoked during this call.
         GLFW.glfwPollEvents()
      }
   }
}