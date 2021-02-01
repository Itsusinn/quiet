package io.github.itsusinn.extension.org.lwjgl.components

abstract class AbstractComponent : IComponent {
    override val gameObject: GameObject

    constructor(gameObject: GameObject) {
        this.gameObject = gameObject
    }

    override fun start() {}
    override fun update(dt: Float) {}
}
