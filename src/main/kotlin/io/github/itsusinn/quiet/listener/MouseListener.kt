package io.github.itsusinn.quiet.listener

import io.github.itsusinn.extension.org.lwjgl.GlfwWindow
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import kotlinx.coroutines.CoroutineScope
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object MouseListener : CoroutineScope by SingleThreadCoroutineScope("glfw-input") {
    private var scrollX = 0.0
    private var scrollY = 0.0
    private var xPos = 0.0
    private var yPos = 0.0

    private var lastX = 0.0
    private var lastY = 0.0
    private val mouseButtonPressed = ArrayList<Boolean>(3).apply {
        repeat(3) {
            add(false)
        }
    }
    private var isDragging = false

    fun getX() = xPos
    fun getY() = yPos
    fun getDx() = lastX - xPos
    fun getDy() = lastY - yPos
    fun getScrollX() = scrollX
    fun getScrollY() = scrollY
    fun isDragging() = isDragging

    fun mouseButtonDown(button: Int): Boolean {
        return mouseButtonPressed.getOrNull(button) ?: false
    }

    fun mousePosCallback(
        window: GlfwWindow,
        xpos: Double,
        ypos: Double
    ) {
        lastX = xPos
        lastY = yPos
        xPos = xpos
        yPos = ypos
        isDragging = mouseButtonPressed.firstOrNull { it == true } ?: false
    }

    fun mouseButtonCallback(
        window: GlfwWindow,
        button: Int,
        action: Int,
        mods: Int
    ) {
        if (button > mouseButtonPressed.size) return
        if (action == GLFW_PRESS) {
            mouseButtonPressed[button] = true
        } else if (action == GLFW_RELEASE) {
            mouseButtonPressed[button] = false
            isDragging = false
        }
    }

    fun mouseScrollCallback(
        window: GlfwWindow,
        xOffset: Double,
        yOffset: Double
    ) {
        scrollX = xOffset
        scrollY = yOffset
    }

    fun endFrame() {
        scrollX = 0.0
        scrollY = 0.0
        lastX = xPos
        lastY = yPos
    }
}
