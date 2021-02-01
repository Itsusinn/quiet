package io.github.itsusinn.extension.org.lwjgl.shader

import mu.KotlinLogging
import org.lwjgl.opengl.GL30.* // ktlint-disable no-wildcard-imports

private val logger = KotlinLogging.logger { }

/**
 * 片段着色器的主要目的是计算一个像素的最终颜色，
 * 这也是所有OpenGL高级效果产生的地方。
 * 通常，片段着色器包含3D场景的数据（比如光照、阴影、光的颜色等等），这些数据可以被用来计算最终像素的颜色
 */
class FragmentShader(
    val source: String
) : Shader {

    override val shaderID: Int by lazy {
        glCreateShader(GL_FRAGMENT_SHADER).also { id ->
            glShaderSource(id, source)
        }
    }

    override fun compile() {
        glCompileShader(shaderID)
        // check error in compilation
        checkCompile { "Error happened in fragment shader compilation" }
    }

    companion object {
        fun create(source: ShaderSource): FragmentShader =
            FragmentShader(source.parse("fragment"))
    }
}
