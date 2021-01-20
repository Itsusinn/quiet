package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.GlfwWorker

suspend fun main(){
   val context = GlfwWorker()
   context.run()
}
