package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.camera.Camera
import io.github.itsusinn.extension.org.lwjgl.render.Scene

class BaseScene : Scene(Camera()) {
    override fun init() {
    }

    override fun update(dt: Float) {
    }
}
