package io.github.itsusinn.extension.java.thread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.*
import kotlin.coroutines.CoroutineContext

fun Thread(name:String,runnable: Runnable) = Thread(runnable,name)

class SingleThread private constructor(
   val name: String
   ):CoroutineScope {

   val executor: ExecutorService = Executors.newSingleThreadExecutor()
   override val coroutineContext = executor.asCoroutineDispatcher()

   fun shutdown() {
      executor.shutdown()
   }

   companion object Factory{
      fun create(name: String): SingleThread {
         return SingleThread(name)
      }
   }
}