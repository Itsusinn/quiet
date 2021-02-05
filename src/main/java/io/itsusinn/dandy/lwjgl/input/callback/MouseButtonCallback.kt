package io.itsusinn.dandy.lwjgl.input.callback

fun interface MouseButtonCallback {
    /**
     * Will be called when a mouse button is pressed or released.
     *
     * @param window the window that received the event
     * @param button the mouse button that was pressed or released
     * @param action the button action. One of:<br></br><table><tr><td>[PRESS][GLFW.GLFW_PRESS]</td><td>[RELEASE][GLFW.GLFW_RELEASE]</td><td>[REPEAT][GLFW.GLFW_REPEAT]</td></tr></table>
     * @param mods   bitfield describing which modifiers keys were held down
     */
    operator fun invoke(button: Int, action: Int, mods: Int)
}
