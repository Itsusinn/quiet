package io.github.itsusinn.extension.org.lwjgl.components

import org.joml.Vector2f
import kotlin.reflect.full.isSubclassOf

data class Transform(
    val position: Vector2f = Vector2f(0f, 0f),
    val scale: Vector2f = Vector2f(1f, 1f)
)

class GameObject(
    val name: String = "Nameless game-object",
    val transform: Transform = Transform()
) {
    val components = ArrayList<IComponent>()

    fun addComponent(component: IComponent) = components.add(component)

    inline fun <reified T> getComponent(): T? {
        for (component in components) {
            if (component::class.isSubclassOf(T::class))
                return component as T
        }
        return null
    }
    inline fun <reified T> removeComponent() = components.removeIf {
        it::class.isSubclassOf(T::class)
    }
}
