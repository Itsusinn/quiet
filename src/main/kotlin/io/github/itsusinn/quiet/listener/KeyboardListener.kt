package io.github.itsusinn.quiet.listener

import io.github.itsusinn.extension.org.lwjgl.GlfwWindow
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.extension.thread.SingleThreadPoolExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.lwjgl.glfw.GLFW.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

object KeyboardListener: CoroutineScope by SingleThreadCoroutineScope("glfw-input") {
   private val logger = KotlinLogging.logger {  }

   private val keyPressed = ArrayList<Boolean>(350).apply {
      repeat(350){
         add(false)
      }
   }
   private val predicates = HashMap<(List<Boolean>) -> Boolean,Int>()
   private val handlerCounter = AtomicInteger(0)
   private val handlers = HashMap<Int,suspend (List<Boolean>) -> Unit>()

   suspend fun registerHandler(
      predicate:(List<Boolean>) -> Boolean,
      handler:suspend (List<Boolean>) -> Unit,
   ):Int = withContext(coroutineContext){
      val handlerID = handlerCounter.getAndIncrement()
      predicates.put(predicate,handlerID)
      handlers.put(handlerID,handler)
      return@withContext handlerID
   }

   suspend fun unregisterHandler(
      id:Int
   ) = withContext(coroutineContext){
      handlers.remove(id)
      var forRemove:((List<Boolean>) -> Boolean)? = null
      predicates.forEach { predicate, handlerID ->
         if (id == handlerID){
            forRemove = predicate
            return@forEach
         }
      }
      if (forRemove == null) return@withContext
      predicates.remove(forRemove)
   }

   fun keyboardCallback(window: GlfwWindow, key: Int, scancode: Int, action: Int, mods: Int){
      when(action){
         GLFW_PRESS -> {
            keyPressed[key] = true
         }
         GLFW_RELEASE -> {
            keyPressed[key] = false
         }
      }
      async {
         pollEvents()
      }
   }

   suspend fun pollEvents() = withContext(coroutineContext) {
      try {
         for ((predicate,id) in predicates){
            if (predicate.invoke(keyPressed)){
               handlers.get(id)?.invoke(keyPressed)
            }
         }
      }catch (e:Throwable){
         logger.warn(e) { "uncaught err \n ${e.stackTrace}" }
      }

   }
}