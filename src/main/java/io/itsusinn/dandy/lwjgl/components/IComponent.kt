package io.itsusinn.dandy.lwjgl.components

interface IComponent {
    val gameObject: GameObject
    fun start()
    fun update(dt: Float)
}
