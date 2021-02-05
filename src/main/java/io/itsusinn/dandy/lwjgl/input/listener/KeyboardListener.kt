@file:Suppress("UNUSED_PARAMETER")
package io.itsusinn.dandy.lwjgl.input.listener

import io.itsusinn.dandy.thread.SingleThreadCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.lwjgl.glfw.GLFW.* // ktlint-disable no-wildcard-imports
import java.util.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

object KeyboardListener : CoroutineScope by SingleThreadCoroutineScope("glfw-input") {
    private val logger = KotlinLogging.logger { }

    val keyPressed = ArrayList<Boolean>(350).apply {
        repeat(350) {
            add(false)
        }
    }
    private val predicates = HashMap<(List<Boolean>) -> Boolean, Int>()
    private val handlerCounter = AtomicInteger(0)
    private val handlers = HashMap<Int, suspend (List<Boolean>) -> Unit>()

    fun keyboard(
        predicate: (List<Boolean>) -> Boolean = { true },
        handler: suspend (List<Boolean>) -> Unit,
    ): Int {
        val handlerID = handlerCounter.getAndIncrement()
        predicates.put(predicate, handlerID)
        handlers.put(handlerID, handler)
        return handlerID
    }

    fun unregisterHandler(
        id: Int
    ) {
        handlers.remove(id)
        var forRemove: ((List<Boolean>) -> Boolean)? = null
        predicates.forEach { predicate, handlerID ->
            if (id == handlerID) {
                forRemove = predicate
                return@forEach
            }
        }
        if (forRemove == null) return
        predicates.remove(forRemove)
    }

    /**
     *  **不要主动调用此方法.**
     *  **此方法应当被注册到[GlfwWindow]的回调中.**
     */
    fun nativeKeyboardCallback(key: Int, scancode: Int, action: Int, mods: Int) {
        when (action) {
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

    private suspend fun pollEvents() = withContext(coroutineContext) {
        try {
            for ((predicate, id) in predicates) {
                if (predicate.invoke(keyPressed)) {
                    handlers.get(id)?.invoke(keyPressed)
                }
            }
        } catch (e: Throwable) {
            logger.warn(e) { "uncaught err \n ${e.stackTrace}" }
        }
    }
}
