package io.itsusinn.dandy.lwjgl.input.callback

import org.lwjgl.glfw.GLFW

fun interface KeyboardCallback {

    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param key      the keyboard key that was pressed or released
     * @param scancode the system-specific scancode of the key
     * @param action   the key action. One of:<br></br><table><tr><td>[PRESS][GLFW.GLFW_PRESS]</td><td>[RELEASE][GLFW.GLFW_RELEASE]</td><td>[REPEAT][GLFW.GLFW_REPEAT]</td></tr></table>
     * @param mods     bitfield describing which modifiers keys were held down
     */
    operator fun invoke(key: Int, scancode: Int, action: Int, mods: Int)
}
