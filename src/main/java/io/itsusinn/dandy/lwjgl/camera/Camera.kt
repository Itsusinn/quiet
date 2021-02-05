package io.itsusinn.dandy.lwjgl.camera

import kotlinx.coroutines.runBlocking
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class Camera(
    val eye: Vector3f = Vector3f(0f, 0f, 20f),
    val center: Vector3f = Vector3f(0f, 0f, -1f),
    val up: Vector3f = Vector3f(0f, 1f, 0f)
) {
    init {
        runBlocking {
//            keyboard { keys ->
//                if (keys[GLFW.GLFW_KEY_W]) {
//                    eye.add(0f, 0f, -10f)
//                } else if (keys[GLFW.GLFW_KEY_S]) {
//                    eye.add(0f, 0f, 10f)
//                }
//                if (keys[GLFW.GLFW_KEY_A]) {
//                    eye.add(-10f, 0f, 0f)
//                } else if (keys[GLFW.GLFW_KEY_D]) {
//                    eye.add(10f, 0f, 0f)
//                }
//                if (keys[GLFW.GLFW_KEY_UP]) {
//                    eye.add(0f, 10f, 0f)
//                } else if (keys[GLFW.GLFW_KEY_DOWN]) {
//                    eye.add(0f, -10f, 0f)
//                }
//            }
        }
    }

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
