package io.github.itsusinn.extension.thread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import java.util.*
import java.util.concurrent.*
import kotlin.coroutines.CoroutineContext

open class CoroutineScopeWithDispatcher(
   override val coroutineContext: CoroutineContext
):CoroutineScope{
   constructor(parent:CoroutineScopeWithDispatcher):this(parent.coroutineContext)
}

/**
 * should keep the reference of its instance,
 * even it needn't be invoked
 */
open class SingleThreadCoroutineScope private constructor(
   private val executor: ExecutorService,
):CoroutineScope {
   override val coroutineContext = executor.asCoroutineDispatcher()

   constructor(
      name: String = UUID.randomUUID().toString()
   ) : this(SingleThreadPoolExecutor(name))

   constructor(parent:SingleThreadCoroutineScope) : this(parent.executor)

}

/**
 * @param[name] Forbid duplication
 */
fun SingleThreadPoolExecutor(name: String) =
   ThreadPoolExecutor(
      1,
      1,
      0L,
      TimeUnit.MILLISECONDS,
      LinkedBlockingQueue(),
      ThreadFactoryWithName(name)
   )
