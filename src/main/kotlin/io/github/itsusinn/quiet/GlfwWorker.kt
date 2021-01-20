package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.*
import io.github.itsusinn.quiet.listener.MouseListener
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.quiet.listener.KeyboardListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.lwjgl.glfw.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {  }

class GlfwWorker:CoroutineScope{
   private val thread = SingleThreadCoroutineScope("glfw-worker")
   override val coroutineContext: CoroutineContext
      get() = thread.coroutineContext

   init {
      runBlocking {
         // Setup an error callback.
         GlfwManager.setErrorCallBack {
            logger.error { "[LWJGL] ${it.error} error\n" }
            logger.error { "\tDescription : ${it.description}" }
            logger.error { "\tStacktrace  :" }
            val stack = it.stack
            for (i in 6 until stack.size) {
               logger.error { "\t\t${stack[i].toString()}" }
            }
         }
         GlfwManager.init()
      }
   }
   private val window: GlfwWindow = createWindow(
      "demo",
      300,
      300,
      "Hello World!"
   )

   suspend fun run() = withContext(coroutineContext) {
      println("Hello LWJGL $LwjglVersion!")

      // Setup a key callback. It will be called every time a key is pressed, repeated or released.
      window.setKeyboardCallback(KeyboardListener::keyboardCallback)
      window.setCursorPosCallback(MouseListener::mousePosCallback)


      val windowSize = window.getWindowSize()
      // Get the resolution of the primary monitor
      val videoMode = GlfwManager.getVideoMode()
      // Center the window
      window.setWindowPos(
         (videoMode!!.width() - windowSize.width) / 2,
         (videoMode.height() - windowSize.height) / 2
      )

      // Make the OpenGL context current
      window.setAsCurrentContext()
      // Enable v-sync
      GLFW.glfwSwapInterval(1)
      // Make the window visible
      window.show()

      //loop() is a blocking method
      loop()
      // Free the window callbacks and destroy the window
      window.freeCallbacks()
      window.destroy()
      // Terminate GLFW and free the error callback
      GlfwManager.terminate()
   }


   private suspend fun loop() = withContext(coroutineContext) {
      // This line is critical for LWJGL's interoperation with GLFW's
      // OpenGL context, or any context that is managed externally.
      // LWJGL detects the context that is current in the current thread,
      // creates the GLCapabilities instance and makes the OpenGL
      // bindings available for use.
      GL.createCapabilities()

      // Set the clear color
      GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)

      // Run the rendering loop until the user has attempted to close
      // the window or has pressed the ESCAPE key.
      while (!window.shouldClose) {
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT) // clear the framebuffer
         window.swapBuffers() // swap the color buffers

         GlfwManager.pollEvents()
      }
   }

}