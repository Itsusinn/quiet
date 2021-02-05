package io.itsusinn.quiet

import glm_.vec4.Vec4
import gln.checkError
import gln.glClearColor
import gln.glViewport
import imgui.DEBUG
import imgui.ImGui
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.gl.glslVersion
import imgui.impl.glfw.ImplGlfw
import io.github.itsusinn.extension.org.lwjgl.* // ktlint-disable no-wildcard-imports
import io.itsusinn.dandy.lwjgl.LwjglVersion
import io.itsusinn.dandy.lwjgl.input.listener.KeyboardListener
import io.itsusinn.dandy.lwjgl.input.listener.KeyboardListener.keyboard
import io.itsusinn.dandy.lwjgl.input.listener.MouseListener
import io.itsusinn.dandy.lwjgl.render.IScene
import mu.KotlinLogging
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger { }

object GlfwApplication : Closeable {

    private val scenes = ConcurrentHashMap<String, IScene>()

    private val window: GlfwWindow
    val ctx: Context
    val implGlfw: ImplGlfw
    val implGl3: ImplGL3

    init {
        glfw {
            errorCallback = { error, description ->
                logger.error { "Glfw Error $error: $description" }
            }
            init()
            windowHint {
                debug = DEBUG
                visible = false

                // Decide GL+GLSL versions
                when (Platform.get()) {
                    Platform.MACOSX -> { // GL 3.2 + GLSL 150
                        glslVersion = 150
                        context.version = "3.2"
                        profile = uno.glfw.windowHint.Profile.core // 3.2+ only
                        forwardComp = true // Required on Mac
                    }
                    else -> { // GL 3.0 + GLSL 130
                        glslVersion = 130
                        context.version = "3.0"
                        // profile = core      // 3.2+ only
                        // forwardComp = true  // 3.0+ only
                    }
                }
            }
        }
        // Create window with graphics context
        window = GlfwWindow(1920, 1080, "Dear ImGui GLFW+OpenGL3 OpenGL example")
        window.makeContextCurrent()
        glfw.swapInterval = VSync.ON
        GL.createCapabilities()
        // Initialize OpenGL loader

        // Setup Dear ImGui context
        ctx = Context()
        // Setup Dear ImGui style
        ImGui.styleColorsDark()
        // Setup Platform/Renderer bindings
        implGlfw = ImplGlfw(window, true)
        implGl3 = ImplGL3()
    }
    val color = Vec4(0.6, 0.6, 0.6, 1f)

    fun run() {
        logger.info { "Hello LWJGL $LwjglVersion!" }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        window.keyCBs["mine"] = KeyboardListener::nativeKeyboardCallback
        window.cursorPosCBs["mine"] = MouseListener::nativeMousePosCallback
        window.mouseButtonCBs["mine"] = MouseListener::mouseButtonCallback
        window.scrollCBs["mine"] = MouseListener::mouseScrollCallback

        keyboard(
            predicate = { keys ->
                keys[GLFW_KEY_ESCAPE] == true
            }
        ) {
            window.shouldClose = true
        }

        val resize = window.size
        // Get the resolution of the primary monitor
        val videoMode = glfw.primaryMonitorVideoMode!!.size
        resize.x = (videoMode.x - resize.x) / 2
        resize.y = (videoMode.y - resize.y) / 2
        // Center the window
        window.pos = resize

        window.show()
        window.loop(GlfwApplication::mainLoop)
    }

    fun mainLoop(stack: MemoryStack) {
        // Start the Dear ImGui frame
        implGl3.newFrame()
        implGlfw.newFrame()

        ImGui.run {
            newFrame()
            begin("Hello, world!") // Create a window called "Hello, world!" and append into it.

            text("This is some useful text.") // Display some text (you can use a format strings too)

            colorEdit3("clear color", color) // Edit 3 floats representing a color

            text("Application average %.3f ms/frame (%.1f FPS)", 1_000f / io.framerate, io.framerate)

            end()
        }
        // Rendering
        ImGui.render()
        glViewport(window.framebufferSize)
        glClearColor(color)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        implGl3.renderDrawData(ImGui.drawData!!)

        if (DEBUG) checkError("mainLoop")
    }

    override fun close() {
        implGl3.shutdown()
        implGlfw.shutdown()
        ctx.destroy()
        window.destroy()
        glfw.terminate()
    }

//    private suspend fun loop() = selfContext {
//
//        scenes.forEach {
//            it.value.init()
//            it.value.start()
//        }
//        var begin: Float
//        var end: Float
//        gln.glClearColor(color)
//
//        glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
//        // glEnable(GL_DEPTH_TEST)
//
//        gl.blend = true
//        gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
//
//        while (!window.shouldClose) {
//            begin = now()
//
//            window.swapBuffers() // swap the color buffers
//
//            GlfwManager.pollEvents()
//
//            end = now()
//            val dt = end - begin
//            begin = end
//
//            // clear the framebuffer
//            glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
//            scenes.get(display)?.update(dt)
//        }
//    }
}
