package io.github.itsusinn.quiet

import kotlinx.coroutines.coroutineScope

suspend fun main() = coroutineScope {

    GlfwWorker.putScene("test", LevelEditorScene())
    GlfwWorker.displayScene("test")
    GlfwWorker.run()
}
