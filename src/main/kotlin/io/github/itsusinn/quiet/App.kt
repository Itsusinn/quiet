package io.github.itsusinn.quiet

import io.github.itsusinn.extension.thread.Thread

object App {
   @JvmStatic fun main(args : Array<String>){
      Thread("lwjwl"){
         HelloWorld().run()
      }.start()
   }
}