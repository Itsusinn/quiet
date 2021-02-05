package io.github.itsusinn.extension.org.lwjgl.camera

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(
    val eye: Vector3f = Vector3f(0f, 0f, 20f),
    val center: Vector3f = Vector3f(0f, 0f, -1f),
    val up: Vector3f = Vector3f(0f, 1f, 0f)
) {

    val projectionMatrix = Matrix4f()
        .identity()
        .perspective(1f, 16 / 9f, 0f, 400f)

    private val _viewMatrix = Matrix4f()

    val viewMatrix
        get() = _viewMatrix.identity().lookAt(
            eye.x, eye.y, eye.z,
            center.x + eye.x, center.y + eye.y, center.z,
            up.x, up.y, up.z
        )!!
}
