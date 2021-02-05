package io.itsusinn.dandy.lwjgl.input.callback

fun interface ScrollCallback {
    /**
     * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
     *
     * @param window  the window that received the event
     * @param xOffset the scroll offset along the x-axis
     * @param yOffset the scroll offset along the y-axis
     */
    operator fun invoke(xOffset: Double, yOffset: Double)
}
