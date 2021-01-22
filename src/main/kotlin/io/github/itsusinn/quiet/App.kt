package io.github.itsusinn.quiet

suspend fun main(){
   val context = GlfwWorker()
   context.putScene("test",LevelEditorScene())
   context.displayScene("test")
   context.run()
}
