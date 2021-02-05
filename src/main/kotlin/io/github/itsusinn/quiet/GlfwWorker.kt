package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.* // ktlint-disable no-wildcard-imports
import io.github.itsusinn.extension.org.lwjgl.render.IScene
import io.github.itsusinn.extension.org.lwjgl.unit.now
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.quiet.listener.KeyboardListener
import io.github.itsusinn.quiet.listener.MouseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import net.mamoe.kjbb.JvmBlockingBridge
import org.lwjgl.glfw.GLFW.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger { }

object GlfwWorker : CoroutineScope {
    private val thread = SingleThreadCoroutineScope("glfw-worker")
    override val coroutineContext: CoroutineContext
        get() = thread.coroutineContext

    private val scenes = ConcurrentHashMap<String, IScene>()

    // val imguiContext by lazy { Context() }

    // val implGl3: ImplGL3 by lazy { ImplGL3() }

    fun putScene(name: String, scene: IScene) {
        scenes.put(name, scene)
    }
    private var display: String = ""

    fun displayScene(name: String) {
        display = name
    }
    fun currentScene() = scenes[display]!!

    init {
        runBlocking {
            // Setup an error callback.
            GlfwManager.setErrorCallBack {
                logger.error { "[LWJGL] ${it.error} error\n" }
                logger.error { "\tDescription : ${it.description}" }
                logger.error { "\tStacktrace  :" }
                val stack = it.stack
                for (i in 6 until stack.size) {
                    logger.error { "\t\t${stack[i]}" }
                }
            }
            GlfwManager.init()
        }
    }
    private val window: GlfwWindow = createWindow(
        "demo",
        1600,
        900,
        "Hello World!"
    )

    @JvmBlockingBridge
    suspend fun run() = withContext(coroutineContext) {
        logger.info { "Hello LWJGL $LwjglVersion!" }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        window.setKeyboardCallback(KeyboardListener::keyboardCallback)
        window.setCursorPosCallback(MouseListener::mousePosCallback)
        window.setMouseButtonCallback(MouseListener::mouseButtonCallback)
        window.setScrollCallback(MouseListener::mouseScrollCallback)

        KeyboardListener.registerHandler(
            predicate = { keys ->
                keys[GLFW_KEY_ESCAPE] == true
            },
            handler = {
                window.shouldClose = true
            }
        )

        val windowSize = window.windowSize
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
        GL.createCapabilities()

        loop()

        window.freeCallbacks()
        window.destroy()
        GlfwManager.terminate()
    }

    private suspend fun loop() = withContext(coroutineContext) {

        scenes.forEach {
            it.value.init()
            it.value.start()
        }
        var begin: Float
        var end: Float

        glClearColor(0.5F, 0.5F, 0.5F, 1F)
        // glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        // glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        while (!window.shouldClose) {
            begin = now()

            window.swapBuffers() // swap the color buffers

            GlfwManager.pollEvents()

            end = now()
            val dt = end - begin
            begin = end

            // clear the framebuffer
            glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
            scenes.get(display)?.update(dt)
        }
    }
}
