@file:Suppress("NOTHING_TO_INLINE")
package io.itsusinn.dandy.lwjgl.components

import org.joml.AxisAngle4f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

/**
 * 累积变换,Model
 */
data class ModelTransformation(
    val translation: Vector3f = Vector3f(0f),
    val rotation: AxisAngle4f = AxisAngle4f(0f, 1f, 1f, 1f),
    val scaling: Vector3f = Vector3f(1f)
) {
    private val transformation = Matrix4f().identity()

    private val cacheDest = Vector4f()
    private val cacheResult = SingletonResult()

    data class SingletonResult(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f
    ) {
        inline fun fillBy(r: Vector4f): SingletonResult {
            x = r.x
            y = r.y
            z = r.z
            return this
        }
    }

    /**
     * **必须使用自动解构获取返回值**
     */
    fun transform(v: Vector4f): SingletonResult = synchronized(cacheResult) call@{
        transformation
            .translate(translation)
            .rotate(rotation)
            .scale(scaling)
            .transform(v, cacheDest)
        return@call cacheResult.fillBy(cacheDest)
    }
}
