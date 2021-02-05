package io.itsusinn.dandy.lwjgl.render

import io.itsusinn.dandy.lwjgl.components.ModelTransformation
import io.itsusinn.dandy.lwjgl.texture.Texture
import io.itsusinn.dandy.lwjgl.unit.Coordinate
import io.itsusinn.dandy.lwjgl.unit.TexCoordinate
import org.joml.Vector4f
import java.util.concurrent.ConcurrentHashMap

annotation class CacheReturn

/**
 * @param[texCoords]纹理座标，指精灵从中截取的u,v座标
 */
data class Sprite(
    val texture: Texture = Texture.cacheCreate(),
    val texCoords: TexCoordinate = TexCoordinate()
)

data class SpriteRender(
    val modelTransformation: ModelTransformation,
    val color: Vector4f = Vector4f(1f, 1f, 1f, 1f),
    var sprite: Sprite = Sprite(),
    var coords: Coordinate = Coordinate(),
) {
    var changed = false
}
class SpriteSheet(
    val texture: Texture = Texture.cacheCreate(),
    val columns: Int = 8,
    val rows: Int = 2,
    val numSprites: Int = 16,
    val spacing: Int = 0
) {
    private val spriteList = ConcurrentHashMap<Int, Sprite>(numSprites / 4)

    @CacheReturn
    fun cut(order: Int): Sprite = spriteList.getOrPut(order) {
        val row = order / columns + 1
        val column = order % columns
        return@getOrPut cut(order, row, column)
    }

    @CacheReturn
    fun cut(row: Int, column: Int): Sprite {
        val order = (row - 1) * columns + rows
        return spriteList.getOrPut(order) { cut(order, row, column) }
    }

    @CacheReturn
    fun cut(
        order: Int,
        row: Int,
        column: Int
    ): Sprite = spriteList.getOrPut(order) {
        val unitX = 1 / columns.toFloat()
        val unitY = 1 / rows.toFloat()

        val rightX = column * unitX
        val leftX = rightX - unitX
        val topY = 1f - (row - 1) * unitY
        val bottomY = topY - unitY

        Sprite(texture, TexCoordinate(rightX, leftX, topY, bottomY))
    }
    companion object {
        private val cacheInstance = ConcurrentHashMap<Int, SpriteSheet>(4)
        @CacheReturn
        fun cacheCreate(
            texture: Texture = Texture.cacheCreate(),
            spriteWidth: Int = 8,
            spriteHeight: Int = 2,
            numSprites: Int = spriteHeight * spriteWidth,
            spacing: Int = 0
        ): SpriteSheet {
            return cacheInstance.getOrPut(texture.textureID) {
                SpriteSheet(texture, spriteWidth, spriteHeight, numSprites, spacing)
            }
        }
    }
}
