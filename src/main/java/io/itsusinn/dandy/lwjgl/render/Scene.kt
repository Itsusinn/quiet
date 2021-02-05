@file:Suppress("NOTHING_TO_INLINE")
package io.itsusinn.dandy.lwjgl.render

import io.itsusinn.dandy.lwjgl.camera.Camera
import io.itsusinn.dandy.lwjgl.components.GameObject
import io.itsusinn.dandy.lwjgl.components.SpriteComponent
import kotlinx.coroutines.CoroutineScope
import java.util.ArrayList

interface IScene : CoroutineScope {
    val camera: Camera
    val gameObjects: MutableList<GameObject>
    val renderer: Renderer

    suspend fun init()

    suspend fun start()

    suspend fun update(dt: Float)
}

abstract class AbstractScene(
    override val camera: Camera = Camera(),
    override val gameObjects: MutableList<GameObject> = ArrayList(),
    override val renderer: Renderer = Renderer(camera)
) : IScene {

    // protected val renderer = Renderer()
    private val isRunning = false

    inline operator fun GameObject.unaryPlus(): GameObject {
        addGameObject(this)
        return this
    }

    final fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
        gameObject.getComponent<SpriteComponent>()?.let {
            renderer.addSpriteRender(it.spriteRender)
        }
    }

    final override suspend fun init() {
        for (gameObject in gameObjects) {
            gameObject.init()
        }
        onInit()
        renderer.init()
    }
    abstract suspend fun onInit()

    final override suspend fun start() {
        for (gameObject in gameObjects) {
            gameObject.start()
        }
        onStart()
    }
    abstract suspend fun onStart()

    final override suspend fun update(dt: Float) {
        for (gameObject in gameObjects) {
            gameObject.update(dt)
        }
        renderer.draw()
        onUpdate(dt)
    }

    abstract suspend fun onUpdate(dt: Float)
}
