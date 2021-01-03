package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.*
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

   override val coroutineContext by lazy { window.coroutineContext }

   fun run() {
      println("Hello LWJGL $LwjglVersion!")
      init()
      //loop() is a blocking method
      loop()
      // Free the window callbacks and destroy the window
      window.freeCallbacks()
      window.destroy()
      // Terminate GLFW and free the error callback
      GlfwManager.terminate()
   }

   private fun init() {
      // Setup an error callback.
      GlfwManager.setErrorCallBack {
         System.err.printf("[LWJGL] %s error\n", it.error)
         System.err.println("\tDescription : ${it.description}")
         System.err.println("\tStacktrace  :")
         val stack = it.stack
         for (i in 4 until stack.size) {
            System.err.print("\t\t")
            System.err.println(stack[i].toString())
         }
      }
      GlfwManager.init()

      // Create the window
      window = createWindow("demo",300, 300, "Hello World!")

      // Setup a key callback. It will be called every time a key is pressed, repeated or released.
      window.setKeyboardCallback {
         if (it.key == GLFW.GLFW_KEY_ESCAPE && it.action == GLFW.GLFW_RELEASE) {
            window.shouldClose = true
         }
         // We will detect this in the rendering loop
      }
      val windowSize = window.getWindowSize()
      // Get the resolution of the primary monitor
      val videoMode = GlfwManager.videoMode
      // Center the window
      window.setWindowPos(
         (videoMode.width() - windowSize.width) / 2,
         (videoMode.height() - windowSize.height) / 2
      )

      // Make the OpenGL context current
      setCurrentContext(window)
      // Enable v-sync
      GLFW.glfwSwapInterval(1)
      // Make the window visible
      window.show()
   }

   private fun loop()  {
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

         GlfwManager.pollEvents()
      }
   }
}