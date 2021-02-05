package io.github.itsusinn.extension.org.lwjgl

import io.github.itsusinn.extension.org.lwjgl.input.callback.CursorPosCallback
import io.github.itsusinn.extension.org.lwjgl.input.callback.KeyboardCallback
import io.github.itsusinn.extension.org.lwjgl.input.callback.MouseButtonCallback
import io.github.itsusinn.extension.org.lwjgl.input.callback.ScrollCallback
import io.github.itsusinn.extension.org.lwjgl.memory.stack
import io.github.itsusinn.quiet.extension.org.lwjgl.unit.with
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.mamoe.kjbb.JvmBlockingBridge
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.* // ktlint-disable no-wildcard-imports
import org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback
import kotlin.properties.Delegates

/**
 * a short way to create a window
 */
fun createWindow(
    identifier: String = "default",
    width: Int = 900,
    height: Int = 1600,
    title: CharSequence = "GlfwWindow $LwjglVersion",
    monitor: Long = 0L,
    share: Long = 0L
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
    val identifier: String,
    val width: Int,
    val height: Int,
    val title: CharSequence,
    val monitor: Long,
    val share: Long,
    hintBuilder: () -> Unit = {
        // Configure GLFW 
        glfwDefaultWindowHints() // optional, the current window hints are already the default 
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation 
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable 
    }
) : CoroutineScope by GlfwManager {
    private val dispatcher = coroutineContext
    var handle by Delegates.notNull<Long>()

    init {
        runBlocking {
            withContext(dispatcher) {
                hintBuilder()
                // call original fun which returns handle to create window
                handle = glfwCreateWindow(width, height, title, monitor, share)
                // add a not null check here to make sure the non-null feat of kt
                check(handle != 0L) { "Failed to create the GLFW window" }
            }
        }
    }

    /**
     * the value of the close flag of window.
     * can be called from any thread
     */
    var shouldClose: Boolean
        get() = glfwWindowShouldClose(handle)
        set(value) = glfwSetWindowShouldClose(handle, value)

    /**
     * Destroys the window and its context
     */
    @JvmBlockingBridge
    suspend fun destroy() = withContext(coroutineContext) {
        glfwDestroyWindow(handle)
    }

    /**
     * Sets the position, in screen coordinates, of the upper-left corner of the content area of the windowed mode window.
     * If the window is a full screen window, this function does nothing.
     */
    @JvmBlockingBridge
    suspend fun setWindowPos(xPos: Int, yPos: Int) = withContext(coroutineContext) {
        GLFW.glfwSetWindowPos(handle, xPos, yPos)
    }

    var windowSize
        get() = runBlocking {
            withContext(coroutineContext) {
                stack {
                    val pWidth = mallocInt(1) // int*
                    val pHeight = mallocInt(1) // int*
                    // Get the window size passed to glfwCreateWindow
                    glfwGetWindowSize(handle, pWidth, pHeight)
                    pWidth[0] with pHeight[0]
                }
            }
        }
        set(value) = runBlocking {
            withContext(coroutineContext) {
                glfwSetWindowSize(handle, value.width, value.height)
            }
        }

    /**
     * Sets the key callback of the window, which is called when a key is pressed, repeated or released.
     */
    @JvmBlockingBridge
    suspend inline fun setKeyboardCallback(
        callback: KeyboardCallback
    ) = withContext(coroutineContext) {
        glfwSetKeyCallback(handle) cb@{
            handle: Long,
            key: Int,
            scancode: Int,
            action: Int,
            mods: Int ->
            if (handle != this@GlfwWindow.handle) return@cb
            callback.invoke(this@GlfwWindow, key, scancode, action, mods)
        }
    }

    /**
     * Will be called when the cursor is moved.
     */
    @JvmBlockingBridge
    suspend inline fun setCursorPosCallback(
        callback: CursorPosCallback
    ) = withContext(coroutineContext) {
        glfwSetCursorPosCallback(handle) cb@{
            handle: Long,
            xpos: Double,
            ypos: Double ->
            if (handle != this@GlfwWindow.handle) return@cb
            callback.invoke(this@GlfwWindow, xpos, ypos)
        }
    }
    @JvmBlockingBridge
    suspend inline fun setMouseButtonCallback(
        callback: MouseButtonCallback
    ) = withContext(coroutineContext) {
        glfwSetMouseButtonCallback(handle) cb@{
            handle: Long,
            button: Int,
            action: Int,
            mods: Int ->
            if (handle != this@GlfwWindow.handle) return@cb
            callback.invoke(this@GlfwWindow, button, action, mods)
        }
    }

    suspend fun setScrollCallback(
        callback: ScrollCallback
    ) = withContext(coroutineContext) {
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
    suspend fun show() = withContext(coroutineContext) { glfwShowWindow(handle) }
}

fun GlfwWindow.setAsCurrentContext() = glfwMakeContextCurrent(handle)
fun GlfwWindow.releaseCurrentContext() = glfwMakeContextCurrent(0)
fun GlfwWindow.swapBuffers() = glfwSwapBuffers(handle)
/**
 * Resets all callbacks for the GLFW window to Null
 * and Callback#free frees all previously set callbacks.
 */
fun GlfwWindow.freeCallbacks() = Callbacks.glfwFreeCallbacks(handle)