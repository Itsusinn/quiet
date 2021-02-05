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
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class BaseScene() : AbstractScene(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default
    init {
        camera.eye.set(800f, 450f, 850f)
    }
    override suspend fun onInit() {

        val spriteSheet: SpriteSheet = SpriteSheet
            .cacheCreate(
                Texture.cacheCreate("assets/images/sprite-sheet.png"),
                14,
                2,
                26
            )
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
            + SpriteComponent(sprite = spriteSheet.cut(0))
            launch {
                val spriteRender = getComponent<SpriteComponent>()?.spriteRender ?: return@launch
                val index = AtomicInteger(1)
                while (true) {
                    if (index.get() == 25) { index.set(0) }
                    spriteRender.sprite = spriteSheet.cut(index.getAndIncrement())
                    spriteRender.changed = true
                    delay(200)
                }
            }
        }

        val background = GameObject(
            "Background",
            ModelTransformation(),
            shape = Coordinate(
                Vector4f(1600f, 900f, 0f, 1f),
                Vector4f(1600f, 0f, 0f, 1f),
                Vector4f(0f, 0f, 0f, 1f),
                Vector4f(0f, 900f, 0f, 1f),
            )
        ).components {
            + SpriteComponent(
                sprite = Sprite(
                    Texture.cacheCreate("assets/images/blank.png"),
                    TexCoordinate(16f, 0f, 9f, 0f)
                )
            )
        }
        val floor = GameObject(
            "floor",
            ModelTransformation(),
            shape = Coordinate(
                Vector4f(1600f, 0f, 0f, 1f),
                Vector4f(1600f, 0f, 900f, 1f),
                Vector4f(0f, 0f, 900f, 1f),
                Vector4f(0f, 0f, 0f, 1f),
            )
        ).components {
            + SpriteComponent(
                sprite = Sprite(
                    Texture.cacheCreate("assets/images/stones/DSC_4438.jpeg"),
                    TexCoordinate(1f, 0f, 1f, 0f)
                )
            )
            launch {
            }
        }

        addGameObject(background)
        addGameObject(player)
        addGameObject(floor)
    }

    override suspend fun onStart() {
    }

    override suspend fun onUpdate(dt: Float) {
    }
}
