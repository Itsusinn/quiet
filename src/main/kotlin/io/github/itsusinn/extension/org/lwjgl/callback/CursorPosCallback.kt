package io.github.itsusinn.extension.org.lwjgl.callback

import io.github.itsusinn.extension.org.lwjgl.GlfwWindow

fun interface CursorPosCallback {
    /**
     * Will be called when the cursor is moved.
     *
     * <p>The callback function receives the cursor position, measured in screen coordinates but relative to the top-left corner of the window client area. On
     * platforms that provide it, the full sub-pixel cursor position is passed on.</p>
     *
     * @param window the window that received the event
     * @param xpos   the new cursor x-coordinate, relative to the left edge of the content area
     * @param ypos   the new cursor y-coordinate, relative to the top edge of the content area
     */
    operator fun invoke(window: GlfwWindow, xpos: Double, ypos: Double)
}
