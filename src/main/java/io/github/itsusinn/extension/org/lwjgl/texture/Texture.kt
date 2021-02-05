package io.github.itsusinn.extension.org.lwjgl.texture

import io.github.itsusinn.extension.org.lwjgl.memory.stack // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL11.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL
import org.lwjgl.stb.STBImage.* // ktlint-disable no-wildcard-imports
import kotlin.IllegalArgumentException

class Texture(
    val textureID: Int,
    val weight: Int,
    val height: Int
) {

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, textureID)
    }

    fun unbind() = glBindTexture(GL_TEXTURE_2D, 0)

    companion object {
        val EmptyTexParameterBuilder = {
            // repeat pictures in both directions 
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            // when stretching an image, pixelate 
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            // when shrinking an image ,pixelate 
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 4)
        }

        private val cache = HashMap<String, Texture>()
        fun cacheCreate(
            filepath: String = "assets/images/blank.png",
            texParameterBuilder: () -> Unit = EmptyTexParameterBuilder
        ): Texture = cache.getOrPut(filepath) {
            create(filepath, texParameterBuilder)
        }

        fun create(
            filepath: String,
            texParameterBuilder: () -> Unit = EmptyTexParameterBuilder
        ): Texture {

            val id = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, id)

            texParameterBuilder()

            return stack {
                val pWeight = mallocInt(1)
                val pHeight = mallocInt(1)
                val channels = mallocInt(1)
                stbi_set_flip_vertically_on_load(true)
                val image = stbi_load(filepath, pWeight, pHeight, channels, 0)
                    ?: throw IllegalArgumentException("Could not load image:$filepath")
                when (channels[0]) {
                    3 -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, pWeight[0], pHeight[0], 0, GL_RGB, GL_UNSIGNED_BYTE, image)
                    else -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, pWeight[0], pHeight[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
                }
                stbi_image_free(image)
                Texture(id, pWeight[0], pHeight[0])
            }
        }
    }
}
