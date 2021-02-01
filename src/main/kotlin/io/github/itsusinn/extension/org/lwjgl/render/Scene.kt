package io.github.itsusinn.extension.org.lwjgl.render

import io.github.itsusinn.extension.org.lwjgl.camera.Camera
import io.github.itsusinn.extension.org.lwjgl.components.GameObject
import java.util.ArrayList

abstract class Scene(
    val camera: Camera,
) {
    // protected val renderer = Renderer()
    private val isRunning = false
    protected var gameObjects: List<GameObject> = ArrayList<GameObject>()
    abstract fun init()
    abstract fun update(dt: Float)
}
