package io.github.itsusinn.extension.org.lwjgl

import org.lwjgl.glfw.GLFW

fun setCurrentContext(window: Window) = GLFW.glfwMakeContextCurrent(window.handle)
fun clearCurrentContext() = GLFW.glfwMakeContextCurrent(NullPointer)