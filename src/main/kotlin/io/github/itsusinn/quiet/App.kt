package io.github.itsusinn.quiet

import io.github.itsusinn.extension.java.thread.SingleThread
import io.github.itsusinn.extension.java.thread.Thread
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object App {
   @JvmStatic fun main(args : Array<String>) = runBlocking<Unit>{
      HelloWorld().run()
   }
}