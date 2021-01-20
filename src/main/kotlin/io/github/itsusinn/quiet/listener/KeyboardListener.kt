package io.github.itsusinn.quiet.listener

import io.github.itsusinn.extension.org.lwjgl.GlfwWindow
import org.lwjgl.glfw.GLFW

object KeyboardListener {

   fun keyboardCallback(window: GlfwWindow, key: Int, scancode: Int, action: Int, mods: Int){
      if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
         window.shouldClose = true
      }
   }
}