package io.itsusinn.dandy.lwjgl.render

import io.itsusinn.dandy.lwjgl.camera.Camera

class Renderer(
    val camera: Camera
) {
    val batches = ArrayList<Drawable>()
    fun init() {}
    fun start() {}
    fun addSpriteRender(spriteRender: SpriteRender) {
        var batch = batches.lastOrNull() ?: run {
            val new = RenderBatch(maxBatchNum, camera = camera)
            batches.add(new)
            new
        }
        batch as RenderBatch
        if (!batch.hasRoom()) {
            batch = RenderBatch(maxBatchNum, camera = camera)
            batches.add(batch)
        }
        batch.addSpriteRender(spriteRender)
    }

    fun draw() {
        for (batch in batches) {
            batch.draw()
        }
    }
    companion object {
        const val maxBatchNum = 100
    }
}
