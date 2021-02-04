package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.components.GameObject
import io.github.itsusinn.extension.org.lwjgl.components.ModelTransformation
import io.github.itsusinn.extension.org.lwjgl.components.SpriteComponent
import io.github.itsusinn.extension.org.lwjgl.render.AbstractScene
import io.github.itsusinn.extension.org.lwjgl.render.Sprite
import io.github.itsusinn.extension.org.lwjgl.render.SpriteSheet
import io.github.itsusinn.extension.org.lwjgl.texture.Texture
import io.github.itsusinn.extension.org.lwjgl.unit.Coordinate
import io.github.itsusinn.extension.org.lwjgl.unit.TexCoordinate
import io.github.itsusinn.quiet.listener.KeyboardListener
import kotlinx.coroutines.runBlocking
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.* // ktlint-disable no-wildcard-imports

class BaseScene : AbstractScene() {
    init {
        camera.eye.set(50f, 50f, 20f)
    }
    override fun onInit() {
        runBlocking {
            KeyboardListener.registerHandler(predicate = { true }) { keys ->
                if (keys[GLFW_KEY_W]) {
                    camera.eye.add(0f, 0f, -2f)
                } else if (keys[GLFW_KEY_S]) {
                    camera.eye.add(0f, 0f, 2f)
                }
                if (keys[GLFW_KEY_A]) {
                    camera.eye.add(-2f, 0f, 0f)
                } else if (keys[GLFW_KEY_D]) {
                    camera.eye.add(2f, 0f, 0f)
                }
                if (keys[GLFW_KEY_UP]) {
                    camera.eye.add(0f, 2f, 0f)
                } else if (keys[GLFW_KEY_DOWN]) {
                    camera.eye.add(0f, -2f, 0f)
                }
            }
        }

        val spriteSheet: SpriteSheet = SpriteSheet
            .cacheCreate(
                Texture.cacheCreate("assets/images/sprite-sheet.png"),
                14,
                2,
                26
            )
        val background = GameObject(
            "Background",
            ModelTransformation(),
            shape = Coordinate(
                Vector4f(80f, 60f, 0f, 1f),
                Vector4f(80f, 0f, 0f, 1f),
                Vector4f(0f, 0f, 0f, 1f),
                Vector4f(0f, 60f, 0f, 1f),
            )
        ).components {
            + SpriteComponent(
                sprite = Sprite(
                    Texture.cacheCreate("assets/images/blank.png"),
                    TexCoordinate(1920f, 0f, 1080f, 0f)
                )
            )
        }
        addGameObject(background)
        val player = GameObject(
            "player-1",
            ModelTransformation(),
            shape = Coordinate(
                Vector4f(50f, 50f, 1f, 1f),
                Vector4f(50f, 0f, 1f, 1f),
                Vector4f(0f, 0f, 1f, 1f),
                Vector4f(0f, 50f, 1f, 1f),
            )
        ).components {
            + SpriteComponent(sprite = spriteSheet.cut(5))
        }
        addGameObject(player)
    }

    override fun onStart() {
    }

    override fun onUpdate(dt: Float) {
    }
}
