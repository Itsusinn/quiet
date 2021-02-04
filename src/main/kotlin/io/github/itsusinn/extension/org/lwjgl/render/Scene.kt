package io.github.itsusinn.extension.org.lwjgl.render

import io.github.itsusinn.extension.org.lwjgl.camera.Camera
import io.github.itsusinn.extension.org.lwjgl.components.GameObject
import io.github.itsusinn.extension.org.lwjgl.components.SpriteComponent
import java.util.ArrayList

interface IScene {
    val camera: Camera
    val gameObjects: MutableList<GameObject>
    val renderer: Renderer

    fun init()

    fun start()

    fun update(dt: Float)
}

abstract class AbstractScene(
    override val camera: Camera = Camera(),
    override val gameObjects: MutableList<GameObject> = ArrayList(),
    override val renderer: Renderer = Renderer(camera)
) : IScene {

    // protected val renderer = Renderer()
    private val isRunning = false

    final fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
        gameObject.getComponent<SpriteComponent>()?.let {
            renderer.addSpriteRender(it.spriteRender)
        }
    }

    final override fun init() {
        for (gameObject in gameObjects) {
            gameObject.init()
        }
        onInit()
        renderer.init()
    }
    abstract fun onInit()

    final override fun start() {
        for (gameObject in gameObjects) {
            gameObject.start()
        }
        onStart()
    }
    abstract fun onStart()

    final override fun update(dt: Float) {
        for (gameObject in gameObjects) {
            gameObject.update(dt)
        }
        renderer.draw()
        onUpdate(dt)
    }

    abstract fun onUpdate(dt: Float)
}
