package io.itsusinn.dandy.thread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object GlfwThread : CoroutineScope by SingleThreadCoroutineScope("glfw-main-thread")
inline val Dispatchers.glfw
    get() = GlfwThread.coroutineContext

