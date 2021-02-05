@file:Suppress("NOTHING_TO_INLINE")
package io.itsusinn.dandy.lwjgl.shader

import io.itsusinn.dandy.lwjgl.memory.stack
import mu.KotlinLogging
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL30.* // ktlint-disable no-wildcard-imports
import java.util.* // ktlint-disable no-wildcard-imports

private val logger by lazy { KotlinLogging.logger { } }

class ShaderProgram(
    source: ShaderSource = ShaderSource(),
) {
    private val program by lazy { glCreateProgram() }

    private val vertexShader = VertexShader.create(source)
    private val fragmentShader = FragmentShader.create(source)

    fun use() = glUseProgram(program)
    fun detach() = glUseProgram(0)

    private var warmed = false

    fun warmUp(): ShaderProgram {
        if (warmed) return this
        warmed = true
        vertexShader.compile()
        fragmentShader.compile()

        // link shaders
        attachShader(vertexShader)
        attachShader(fragmentShader)

        link()

        return this
    }

    private inline fun attachShader(shader: Shader) {
        glAttachShader(program, shader.shaderID)
    }

    private inline fun link() {
        glLinkProgram(program)
        checkLink()
    }

    private inline fun checkLink() {
        val success = glGetProgrami(program, GL_LINK_STATUS)
        if (success == GL_FALSE) {
            val len = glGetProgrami(program, GL_INFO_LOG_LENGTH)
            logger.error { "Error happened in linking shaders" }
            logger.error { glGetProgramInfoLog(program, len) }
            throw IllegalStateException()
        }
    }

    fun uploadMatrix4(varName: String, mat4: Matrix4f) {
        val location = glGetUniformLocation(program, varName)
        stack {
            val matBuffer = mallocFloat(16)
            mat4.get(matBuffer)
            glUniformMatrix4fv(location, false, matBuffer)
        }
    }
    fun uploadInt(varName: String, int: Int) {
        val location = glGetUniformLocation(program, varName)
        use()
        glUniform1i(location, int)
    }
    fun uploadFloat(varName: String, float: Float) {
        val location = glGetUniformLocation(program, varName)
        use()
        glUniform1f(location, float)
    }
    fun uploadTexture(varName: String, slot: Int) {
        val location = glGetUniformLocation(program, varName)
        use()
        glUniform1i(location, slot)
    }
    fun uploadIntArray(varName: String, array: IntArray) {
        val varLocation = glGetUniformLocation(program, varName)
        use()
        glUniform1iv(varLocation, array)
    }
}
