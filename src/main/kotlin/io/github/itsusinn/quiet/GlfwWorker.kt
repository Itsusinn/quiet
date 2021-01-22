package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.*
import io.github.itsusinn.extension.org.lwjgl.scene.Scene
import io.github.itsusinn.extension.org.lwjgl.unit.now
import io.github.itsusinn.quiet.listener.MouseListener
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.quiet.listener.KeyboardListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

private val logger = KotlinLogging.logger {  }

class GlfwWorker:CoroutineScope{
   private val thread = SingleThreadCoroutineScope("glfw-worker")
   override val coroutineContext: CoroutineContext
      get() = thread.coroutineContext

   private val scenes = ConcurrentHashMap<String,Scene>()

   fun putScene(name:String,scene: Scene){
      scenes.put(name,scene)
   }
   private var display:String = ""

   fun displayScene(name: String){
      display = name
   }

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
      1280,
      900,
      "Hello World!"
   )

   var r = 1.0f
   var g = 1.0f
   var b = 1.0f
   var a = 1.0f

   suspend fun run() = withContext(coroutineContext) {
      println("Hello LWJGL $LwjglVersion!")

      // Setup a key callback. It will be called every time a key is pressed, repeated or released.
      window.setKeyboardCallback(KeyboardListener::keyboardCallback)
      window.setCursorPosCallback(MouseListener::mousePosCallback)
      window.setMouseButtonCallback(MouseListener::mouseButtonCallback)
      window.setScrollCallback(MouseListener::mouseScrollCallback)

      KeyboardListener.registerHandler(
         predicate = { keys ->
            keys[GLFW_KEY_DOWN] or keys[GLFW_KEY_UP]
         },
         handler = { keys ->
            if (keys[GLFW_KEY_DOWN]){
               r = max(r-0.01f,0f)
               g = max(g-0.01f,0f)
               b = max(b-0.01f,0f)
            }
            if (keys[GLFW_KEY_UP]){
               r = min(r+0.01f,1f)
               g = min(g+0.01f,1f)
               b = min(b+0.01f,1f)
            }
         }
      )

      KeyboardListener.registerHandler(
         predicate = { keys ->
            keys[GLFW_KEY_ESCAPE] == true
         },
         handler = {
            window.shouldClose = true
         }
      )

      val windowSize = window.getWindowSize()
      // Get the resolution of the primary monitor
      val videoMode = GlfwManager.getVideoMode()

      // Center the window
      window.setWindowPos(
         (videoMode!!.width() - windowSize.width) / 2,
         (videoMode.height() - windowSize.height) / 2
      )

      window.setAsCurrentContext()
      glfwSwapInterval(1)
      window.show()
      loop()

      window.freeCallbacks()
      window.destroy()
      GlfwManager.terminate()
   }


   private suspend fun loop() = withContext(coroutineContext) {

      GL.createCapabilities()
      scenes.forEach{
         it.value.init()
      }
      var begin = now()
      var end = now()
      while (!window.shouldClose) {
         begin = now()

         GL11.glClearColor(r, g, b, a)
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT) // clear the framebuffer
         window.swapBuffers() // swap the color buffers

         GlfwManager.pollEvents()

         end = now()
         val dt = end - begin
         begin = end

         scenes.get(display)?.update(dt)
      }
   }

}