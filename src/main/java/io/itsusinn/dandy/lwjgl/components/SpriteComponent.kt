@file:Suppress("NOTHING_TO_INLINE")
package io.itsusinn.dandy.lwjgl.components

import io.itsusinn.dandy.lwjgl.render.Sprite
import io.itsusinn.dandy.lwjgl.render.SpriteRender
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
