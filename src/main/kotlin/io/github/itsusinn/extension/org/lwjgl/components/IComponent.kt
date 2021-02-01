package io.github.itsusinn.extension.org.lwjgl.components

interface IComponent {
    val gameObject: GameObject
    fun start()
    fun update(dt: Float)
}
