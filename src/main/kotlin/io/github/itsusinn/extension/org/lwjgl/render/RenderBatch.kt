package io.github.itsusinn.extension.org.lwjgl.render

import io.github.itsusinn.extension.org.lwjgl.camera.Camera
import io.github.itsusinn.extension.org.lwjgl.components.ModelTransformation
import io.github.itsusinn.extension.org.lwjgl.shader.ShaderProgram
import io.github.itsusinn.extension.org.lwjgl.texture.Texture
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL15.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL20.* // ktlint-disable no-wildcard-imports
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.opengl.GL45.glNamedBufferSubData
import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicBoolean

class RenderBatch(
    val maxBatch: Int = 300,
    val shaderProgram: ShaderProgram = ShaderProgram(),
    val camera: Camera,
) : Drawable {
    val vertexSet = FloatArray(maxBatch * VERTEX_SIZE) { 0f }
    val spriteRenders = ArrayList<SpriteRender>(maxBatch)
    val textures = ArrayList<Texture>(8)
    private val texSlots = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)

    fun hasRoom() = spriteRenders.size < maxBatch
    val initialized = AtomicBoolean(false)

    fun addSpriteRender(spriteRender: SpriteRender) {
        if (!hasRoom()) throw IllegalStateException("Has reached the maximum number of batches")
        spriteRenders.add(spriteRender)
        var index = (spriteRenders.size - 1) * VERTEX_SIZE * 4
        fulfill(index, spriteRender)
    }

    private fun fulfill(index: Int, spriteRender: SpriteRender) {
        // fulfill pos attr
        val modelTrans = spriteRender.modelTransformation::transform
        val (
            rightTop,
            rightBottom,
            leftBottom,
            leftTop,
        ) = spriteRender.coords
        val color = spriteRender.color
        val texCoords = spriteRender.sprite.texCoords
        val texID = run {
            val texture = spriteRender.sprite.texture
            synchronized(textures) {
                if (!textures.contains(texture)) {
                    textures.add(texture)
                }
            }
            var texID: Int = -1
            for ((index2, value) in textures.withIndex()) {
                if (value == texture) texID = index2
            }
            check(texID != -1) { "Could not find texture" }
            return@run texID
        }

        fill(index, rightTop, color, texCoords.rightTop, texID, modelTrans)
        fill(index + VERTEX_SIZE, rightBottom, color, texCoords.rightBottom, texID, modelTrans)
        fill(index + VERTEX_SIZE * 2, leftBottom, color, texCoords.leftBottom, texID, modelTrans)
        fill(index + VERTEX_SIZE * 3, leftTop, color, texCoords.leftTop, texID, modelTrans)
    }
    private fun fill(
        index: Int,
        vertex: Vector4f,
        color: Vector4f,
        texCoords: Vector2f,
        texID: Int,
        modelTrans: (Vector4f) -> ModelTransformation.SingletonResult,
    ) {
        var offset = index
        val (x, y, z) = modelTrans(vertex)
        // fulfill pos attr
        vertexSet[offset++] = x
        vertexSet[offset++] = y
        vertexSet[offset++] = z

        // fulfill color attr
        vertexSet[offset++] = color.x
        vertexSet[offset++] = color.y
        vertexSet[offset++] = color.z
        vertexSet[offset++] = color.w

        // fulfill tex coordinate
        vertexSet[offset++] = texCoords.x
        vertexSet[offset++] = texCoords.y

        // fulfill tex id
        vertexSet[offset] = texID.toFloat()
    }

    private val vaoID by lazy { glGenVertexArrays() }
    // 使用glGenBuffers函数生成一个VBO对象并返回一个缓冲ID
    private val vboID by lazy { glGenBuffers() }
    private val eboID by lazy { glGenBuffers() }

    fun init() {
        if (initialized.get()) return
        initialized.set(true)

        shaderProgram.warmUp()
        // Generate and bind a Vertex Array Object

        glBindVertexArray(vaoID)

        // Allocate space for vertices

        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertexSet.size * Float.SIZE_BYTES.toLong(), GL_DYNAMIC_DRAW)

        // Create and upload indices buffer
        val indices = run {
            val indexSet = IntArray(6 * maxBatch) { 0 }
            for (index in 0 until maxBatch) {
                val offsetArrayIndex = 6 * index
                val offset = 4 * index
                // index: 0, 1, 2, 3 ,4 ,5        6, 7, 8, 9, 10, 11
                // value: 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
                // Triangle 1
                indexSet[offsetArrayIndex] = offset + 3
                indexSet[offsetArrayIndex + 1] = offset + 2
                indexSet[offsetArrayIndex + 2] = offset + 0
                // Triangle 2
                indexSet[offsetArrayIndex + 3] = offset + 0
                indexSet[offsetArrayIndex + 4] = offset + 2
                indexSet[offsetArrayIndex + 5] = offset + 1
            }
            indexSet
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET.toLong())
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET.toLong())
        glEnableVertexAttribArray(1)

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET.toLong())
        glEnableVertexAttribArray(2)

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET.toLong())
        glEnableVertexAttribArray(3)
    }

    override fun draw() {
        if (!initialized.get()) init()
        // re-buffer vertex
        for ((index, spriteRender) in spriteRenders.withIndex()) {
            if (!spriteRender.changed) break
            spriteRender.changed = false
            fulfill(index, spriteRender)
        }

        // rebuffer all data every frame
        glNamedBufferSubData(vboID, 0L, vertexSet)

        // Use shader
        shaderProgram.use()
        shaderProgram.uploadMatrix4("uProjection", camera.projectionMatrix)
        shaderProgram.uploadMatrix4("uView", camera.viewMatrix)

        for (i in textures.indices) {
            glActiveTexture(GL_TEXTURE0 + i)
            textures[i].bind()
        }
        shaderProgram.uploadIntArray("uTextures", texSlots)

        glBindVertexArray(vaoID)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)
        glEnableVertexAttribArray(3)

        glDrawElements(GL_TRIANGLES, spriteRenders.size * 6, GL_UNSIGNED_INT, 0)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)
        glDisableVertexAttribArray(3)
        glBindVertexArray(0)

        for (texture in textures) {
            texture.unbind()
        }
        shaderProgram.detach()
    }
    private companion object {
        const val POS_SIZE = 3
        const val COLOR_SIZE = 4
        const val TEX_COORDS_SIZE = 2
        const val TEX_ID_SIZE = 1
        const val VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE

        const val POS_SIZE_BYTES = POS_SIZE * Float.SIZE_BYTES
        const val COLOR_SIZE_BYTES = COLOR_SIZE * Float.SIZE_BYTES
        const val TEX_COORDS_SIZE_BYTES = TEX_COORDS_SIZE * Float.SIZE_BYTES
        const val TEX_ID_SIZE_BYTES = TEX_ID_SIZE * Float.SIZE_BYTES

        const val VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.SIZE_BYTES

        const val POS_OFFSET = 0
        const val COLOR_OFFSET = POS_SIZE_BYTES
        const val TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE_BYTES
        const val TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE
    }
}
