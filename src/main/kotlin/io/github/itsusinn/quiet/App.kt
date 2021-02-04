package io.github.itsusinn.quiet

import kotlinx.coroutines.coroutineScope

suspend fun main() = coroutineScope {

    GlfwWorker.putScene("test", BaseScene())
    GlfwWorker.displayScene("test")
    GlfwWorker.run()
}
