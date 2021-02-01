package io.github.itsusinn.extension.org.lwjgl.texture

import io.github.itsusinn.extension.org.lwjgl.memory.stack
import org.lwjgl.opengl.GL11.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL11.glGenTextures
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load
import kotlin.IllegalArgumentException
import kotlin.properties.Delegates

val EmptyTexture by lazy {
    Texture("assets/images/test.jpg")
}

class Texture(
    val filepath: String,
    texParameterBuilder: () -> Unit = {
        // repeat pictures in both directions 
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // when stretching an image, pixelate 
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        // when shrinking an image ,pixelate 
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    }
) {
    private var _weight by Delegates.notNull<Int>()
    val weight
        get() = _weight
    private var _height by Delegates.notNull<Int>()
    val height
        get() = _height

    private val textureID by lazy {
        val id = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, id)

        texParameterBuilder()

        stack {
            val pWeight = mallocInt(1)
            val pHeight = mallocInt(1)
            val channels = mallocInt(1)
            val image = stbi_load(filepath, pWeight, pHeight, channels, 0)
                ?: throw IllegalArgumentException("Could not load image")
            when (channels[0]) {
                3 -> {
                    glTexImage2D(
                        GL_TEXTURE_2D, 0, GL_RGB, pWeight[0], pHeight[0], 0, GL_RGB, GL_UNSIGNED_BYTE, image
                    )
                }
                4 -> {
                    glTexImage2D(
                        GL_TEXTURE_2D, 0, GL_RGBA, pWeight[0], pHeight[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, image
                    )
                }
                else -> {
                    stbi_image_free(image)
                    throw IllegalArgumentException("Unsupported image channels")
                }
            }
            _weight = pWeight[0]
            _height = pHeight[0]
            stbi_image_free(image)
        }
        id
    }

    fun bind() = glBindTexture(GL_TEXTURE_2D, textureID)

    fun unbind() = glBindTexture(GL_TEXTURE_2D, 0)
}
