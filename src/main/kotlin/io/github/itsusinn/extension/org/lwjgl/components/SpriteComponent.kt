package io.github.itsusinn.extension.org.lwjgl.components

import io.github.itsusinn.extension.org.lwjgl.texture.Texture
import org.joml.Vector2f

data class Coordinate(
    val first: Vector2f,
    val second: Vector2f,
    val third: Vector2f,
    val fourth: Vector2f
)

class SpriteComponent(
    val texture: Texture,
    val texCoords: Coordinate = Coordinate(
        Vector2f(1f, 1f),
        Vector2f(1f, 0f),
        Vector2f(0f, 0f),
        Vector2f(0f, 1f)
    ),
    gameObject: GameObject
) : AbstractComponent(gameObject) {

    override fun update(dt: Float) {
    }
}
