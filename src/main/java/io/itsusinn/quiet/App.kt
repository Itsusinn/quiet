package io.itsusinn.quiet

import kotlinx.coroutines.coroutineScope

suspend fun main() = coroutineScope {

    // GlfwWorker.putScene("test", BaseScene())
    // GlfwWorker.displayScene("test")
    GlfwApplication.use {
        it.run()
    }
}
