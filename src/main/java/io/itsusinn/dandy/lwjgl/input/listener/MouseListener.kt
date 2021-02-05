@file:Suppress("UNUSED_PARAMETER")
package io.itsusinn.dandy.lwjgl.input.listener

import glm_.vec2.Vec2
import glm_.vec2.Vec2d
import io.itsusinn.dandy.thread.SingleThreadCoroutineScope
import kotlinx.coroutines.CoroutineScope
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object MouseListener : CoroutineScope by SingleThreadCoroutineScope("glfw-input") {
    private var scrollX = 0.0
    private var scrollY = 0.0
    private var xPos = 0.0f
    private var yPos = 0.0f

    private var lastX = 0.0f
    private var lastY = 0.0f
    private val mouseButtonPressed = ArrayList<Boolean>(4).apply {
        repeat(8) { add(false) }
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

    /**
     *  **不要主动调用此方法.**
     *  **此方法应当被注册到[GlfwWindow]的回调中.**
     */
    fun nativeMousePosCallback(
        pos: Vec2
    ) {
        lastX = xPos
        lastY = yPos
        xPos = pos.x
        yPos = pos.y
        isDragging = mouseButtonPressed.firstOrNull { it == true } ?: false
    }

    /**
     *  **不要主动调用此方法.**
     *  **此方法应当被注册到[GlfwWindow]的回调中.**
     */
    fun mouseButtonCallback(
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

    /**
     *  **不要主动调用此方法.**
     *  **此方法应当被注册到[GlfwWindow]的回调中.**
     */
    fun mouseScrollCallback(
        offset: Vec2d
    ) {
        scrollX = offset.x
        scrollY = offset.y
    }

    fun endFrame() {
        scrollX = 0.0
        scrollY = 0.0
        lastX = xPos
        lastY = yPos
    }
}
