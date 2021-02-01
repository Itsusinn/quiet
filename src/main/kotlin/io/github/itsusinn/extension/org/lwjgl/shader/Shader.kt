package io.github.itsusinn.extension.org.lwjgl.shader

import mu.KotlinLogging
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.* // ktlint-disable no-wildcard-imports
import java.lang.IllegalStateException

private val logger by lazy { KotlinLogging.logger { } }

interface Shader {
    public val shaderID: Int
    fun compile()
    fun checkCompile(msg: () -> String) {
        var success = glGetShaderi(shaderID, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val len = GL30.glGetShaderi(shaderID, GL30.GL_INFO_LOG_LENGTH)
            logger.error(msg)
            logger.error { glGetShaderInfoLog(shaderID, len) }
            throw IllegalStateException()
        }
    }
}
