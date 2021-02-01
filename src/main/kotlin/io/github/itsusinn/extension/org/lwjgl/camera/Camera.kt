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
        .ortho(0f, 32f * 40f, 0f, 32f * 21f, 0f, 100f)

    private val _viewMatrix = Matrix4f().identity()

    val viewMatrix
        get() = _viewMatrix.lookAt(eye, center, up)!!
}
