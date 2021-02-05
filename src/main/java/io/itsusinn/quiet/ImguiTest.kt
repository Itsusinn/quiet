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
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw

fun main() {
    ImguiTest
}

object ImguiTest {
    val window: GlfwWindow
    val ctx: Context

    var f = 0f
    val clearColor = Vec4(0.45f, 0.55f, 0.6f, 1f)
    // Our state
    var showAnotherWindow = false
    var showDemoWindow = true
    var counter = 0

    val implGlfw: ImplGlfw
    val implGl3: ImplGL3

    init {
        // Setup window
        glfw {
            errorCallback = { error, description -> println("Glfw Error $error: $description") }
            init()
            windowHint {
                debug = DEBUG

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
        window = GlfwWindow(1280, 720, "Dear ImGui GLFW+OpenGL3 OpenGL example")
        window.makeContextCurrent()
        glfw.swapInterval = VSync.ON // Enable vsync

        // Initialize OpenGL loader
        GL.createCapabilities()

        // Setup Dear ImGui context
        ctx = Context()
        // Setup Dear ImGui style
        ImGui.styleColorsDark()
        // ImGui.styleColorsClassic()

        // Setup Platform/Renderer bindings
        implGlfw = ImplGlfw(window, true)
        implGl3 = ImplGL3()

        window.loop(::mainLoop)

        implGl3.shutdown()
        implGlfw.shutdown()
        ctx.destroy()
//        GL.destroy()
        window.destroy()
        glfw.terminate()
    }
    @JvmStatic fun main(args: Array<String>) {
    }
    fun mainLoop(stack: MemoryStack) {

        // Start the Dear ImGui frame
        implGl3.newFrame()
        implGlfw.newFrame()

        ImGui.run {

            newFrame()

            // 1. Show the big demo window (Most of the sample code is in ImGui::ShowDemoWindow()! You can browse its code to learn more about Dear ImGui!).
            if (showDemoWindow)
                showDemoWindow(::showDemoWindow)

            // 2. Show a simple window that we create ourselves. We use a Begin/End pair to created a named window.
            run {

                begin("Hello, world!") // Create a window called "Hello, world!" and append into it.

                text("This is some useful text.") // Display some text (you can use a format strings too)
                checkbox("Demo Window", ::showDemoWindow) // Edit bools storing our window open/close state
                checkbox("Another Window", ::showAnotherWindow)

                sliderFloat("float", ::f, 0f, 1f) // Edit 1 float using a slider from 0.0f to 1.0f
                colorEdit3("clear color", clearColor) // Edit 3 floats representing a color

                if (button("Button")) // Buttons return true when clicked (most widgets return true when edited/activated)
                    counter++

                /*  Or you can take advantage of functional programming and pass directly a lambda as last parameter:
                    button("Button") { counter++ }                */

                sameLine()
                text("counter = $counter")

                text("Application average %.3f ms/frame (%.1f FPS)", 1_000f / io.framerate, io.framerate)

                end()

                // 3. Show another simple window.
                if (showAnotherWindow) {
                    // Pass a pointer to our bool variable (the window will have a closing button that will clear the bool when clicked)
                    begin("Another Window", ::showAnotherWindow)
                    text("Hello from another window!")
                    if (button("Close Me"))
                        showAnotherWindow = false
                    end()
                }
            }
        }

        // Rendering
        ImGui.render()
        glViewport(window.framebufferSize)
        glClearColor(clearColor)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        implGl3.renderDrawData(ImGui.drawData!!)

        if (DEBUG) checkError("mainLoop")

//        RemoteryGL.rmt_EndOpenGLSample()
    }
}
