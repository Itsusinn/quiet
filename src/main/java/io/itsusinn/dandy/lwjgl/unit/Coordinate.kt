package io.itsusinn.dandy.lwjgl.unit

import org.joml.Vector2f
import org.joml.Vector4f

data class Coordinate(
    val rightTop: Vector4f = Vector4f(1f, 1f, 0f, 1f),
    val rightBottom: Vector4f = Vector4f(1f, 0f, 0f, 1f),
    val leftBottom: Vector4f = Vector4f(0f, 0f, 0f, 1f),
    val leftTop: Vector4f = Vector4f(0f, 1f, 0f, 1f)
)
data class TexCoordinate(
    val rightTop: Vector2f = Vector2f(1f, 1f),
    val rightBottom: Vector2f = Vector2f(1f, 0f),
    val leftBottom: Vector2f = Vector2f(0f, 0f),
    val leftTop: Vector2f = Vector2f(0f, 1f)
) {
    constructor(
        rightX: Float,
        leftX: Float,
        topY: Float,
        bottomY: Float
    ) : this(
        Vector2f(rightX, topY),
        Vector2f(rightX, bottomY),
        Vector2f(leftX, bottomY),
        Vector2f(leftX, topY)
    )
}
data class TriCoordinate(
    val frontRightTop: Vector4f = Vector4f(1f, 1f, 1f, 1f),
    val frontRightBottom: Vector4f = Vector4f(1f, 0f, 1f, 1f),
    val frontLeftBottom: Vector4f = Vector4f(0f, 0f, 1f, 1f),
    val frontLeftTop: Vector4f = Vector4f(0f, 1f, 1f, 1f),
    val backRightTop: Vector4f = Vector4f(1f, 1f, 0f, 1f),
    val backRightBottom: Vector4f = Vector4f(1f, 0f, 0f, 1f),
    val backLeftBottom: Vector4f = Vector4f(0f, 0f, 0f, 1f),
    val backLeftTop: Vector4f = Vector4f(0f, 1f, 0f, 1f)
)
