@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.extension.org.lwjgl.components

import io.github.itsusinn.extension.org.lwjgl.render.Sprite
import io.github.itsusinn.extension.org.lwjgl.render.SpriteRender
import org.joml.Vector4f

class SpriteComponent(
    override val gameObject: GameObject,
    val spriteRender: SpriteRender
) : AbstractComponent(gameObject) {

    constructor(
        gameObject: GameObject,
        color: Vector4f = Vector4f(1f, 1f, 1f, 1f),
        sprite: Sprite = Sprite(),
    ) : this(
        gameObject,
        SpriteRender(
            gameObject.modelTransformation,
            color,
            sprite,
            gameObject.shape
        )
    )

    override fun update(dt: Float) {
    }
}
inline fun GameObject.SpriteComponent(
    color: Vector4f = Vector4f(1f, 1f, 1f, 1f),
    sprite: Sprite = Sprite()
): SpriteComponent = SpriteComponent(this, color, sprite)
