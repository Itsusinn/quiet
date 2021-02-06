package io.itsusinn.quiet

fun main() {

    // GlfwWorker.putScene("test", BaseScene())
    // GlfwWorker.displayScene("test")
    GlfwApplication.use {
        it.putScene("test", BaseScene())
        it.start()
    }
}
