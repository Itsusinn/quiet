@file:Suppress("UNUSED_PARAMETER")
package io.itsusinn.quiet

import glm_.vec2.Vec2
import glm_.vec4.Vec4
import gln.BlendFactor.Companion.ONE_MINUS_SRC_ALPHA
import gln.BlendFactor.Companion.SRC_ALPHA
import gln.checkError
import gln.gl
import gln.glClearColor
import gln.glViewport
import imgui.DEBUG
import imgui.ImGui
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.gl.glslVersion
import imgui.impl.glfw.ImplGlfw
import io.itsusinn.dandy.lwjgl.LwjglVersion
import io.itsusinn.dandy.lwjgl.input.listener.KeyboardListener
import io.itsusinn.dandy.lwjgl.input.listener.KeyboardListener.keyboard
import io.itsusinn.dandy.lwjgl.input.listener.MouseListener
import io.itsusinn.dandy.lwjgl.render.IScene
import io.itsusinn.dandy.lwjgl.texture.Texture
import io.itsusinn.dandy.lwjgl.unit.now
import mu.KotlinLogging
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.* // ktlint-disable no-wildcard-imports
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.Delegates

private val logger = KotlinLogging.logger { }

object GlfwApplication : Closeable {

    private val scenes = ConcurrentHashMap<String, IScene>()

    private val window: GlfwWindow
    val ctx: Context
    val implGlfw: ImplGlfw
    val implGl3: ImplGL3
    lateinit var testTexture: Texture

    var showAnotherWindow = false
    var showDemoWindow = true
    var counter = 0

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
    val clearColor = Vec4(0.6, 0.6, 0.6, 1f)

    var fboID by Delegates.notNull<Int>()
    lateinit var framebufferTex: Texture

    fun start() {
        logger.info { "Hello LWJGL $LwjglVersion!" }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        window.keyCBs["mine"] = KeyboardListener::nativeKeyboardCallback
        window.cursorPosCBs["mine"] = MouseListener::nativeMousePosCallback
        window.mouseButtonCBs["mine"] = MouseListener::nativeMouseButtonCallback
        window.scrollCBs["mine"] = MouseListener::nativeMouseScrollCallback

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
        testTexture = Texture.create("assets/images/139921.png")

        createFrameBuffer()

        window.show()

        glViewport(0, 0, 1920, 1080)

        gl.blend = true
        gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
        scenes.forEach {
            it.value.init()
            it.value.start()
        }

        window.loop(GlfwApplication::mainLoop)
    }

    fun putScene(name: String, scene: IScene) {
        scenes.put(name, scene)
    }

    var begin: Float = now()
    var end: Float = now()
    var dt = 1 / 60f
    fun mainLoop(stack: MemoryStack) {
        begin = now()

        glClearColor(clearColor)

        glBindFramebuffer(GL_FRAMEBUFFER, fboID)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        // gl.depthTest = true
        // i don't know why it flushes if i enable depth test

        scenes.forEach {
            it.value.update(dt)
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glClear(GL_COLOR_BUFFER_BIT)

        // Start the Dear ImGui frame
        implGl3.newFrame()
        implGlfw.newFrame()

        ImGui.run {

            newFrame()

            // 1. Show the big demo window (Most of the sample code is in ImGui::ShowDemoWindow()! You can browse its code to learn more about Dear ImGui!).
            if (showDemoWindow)
                showDemoWindow(::showDemoWindow)

            // 2. Show a simple window that we create ourselves. We use a Begin/End pair to created a named window.

            begin("Hello, world!")
            // Create a window called "Hello, world!" and append into it.

            text("This is some useful text.")
            // Display some text (you can use a format strings too)
            checkbox("Demo Window", ::showDemoWindow)
            // Edit bool storing our window open/close state
            checkbox("Another Window", ::showAnotherWindow)

            // Edit 1 float using a slider from 0.0f to 1.0f
            colorEdit4("clear color", clearColor)
            // Edit 3 floats representing a color

            if (button("Button"))
                counter++
            // Buttons return true when clicked (most widgets return true when edited/activated)

            /*  Or you can take advantage of functional programming
            and pass directly a lambda as last parameter:
                button("Button") { counter++ }                */

            sameLine()
            text("counter = $counter")

            text("Application average %.3f ms/frame (%.1f FPS)", 1_000f / io.framerate, io.framerate)

            image(
                framebufferTex.textureID,
                Vec2(1600, 900),
                uv0 = Vec2(0, 1),
                uv1 = Vec2(1, 0)
            )

            end()

            // 3. Show another simple window.
            if (showAnotherWindow) {
                // Pass a pointer to our bool variable (the window will have a closing button that will clear the bool when clicked)
                begin("Another Window", ::showAnotherWindow)
                text("Hello from another window!")

                // windowDrawList.addImage()
                if (button("Close Me"))
                    showAnotherWindow = false
                end()
            }
        }
        // Rendering
        ImGui.render()

        glViewport(window.framebufferSize)

        implGl3.renderDrawData(ImGui.drawData!!)

        if (DEBUG) checkError("mainLoop")

        begin = end
        end = now()
        dt = end - begin
    }

    private fun createFrameBuffer() {

        fboID = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fboID)

        // Create texture to render data too and attach it to framebuffer
        framebufferTex = Texture.buffer(1920, 1080)
//        framebufferTex = Texture.cacheCreate("assets/images/139921.png")
        // 通过glFramebufferTexture2D这个API将texture attach到FBO上。
        glFramebufferTexture2D(
            GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, framebufferTex.textureID, 0
        )

        // Create renderbuffer to store depth_stencil info
        val rboID: Int = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, rboID)
        // 通过glRenderbufferStorage API给RBO创建、初始化存储空间
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, 1920, 1080)
        // glFramebufferRenderbuffer API 将指定的RBO关联到GPU当前的FBO上。
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw IllegalStateException("Error: Framebuffer is not complete.")
        }
        glBindRenderbuffer(GL_RENDERBUFFER, 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        if (DEBUG) checkError("frameBuffer")
    }
    override fun close() {
        implGl3.shutdown()
        implGlfw.shutdown()
        ctx.destroy()
        window.destroy()
        glfw.terminate()
    }
}
