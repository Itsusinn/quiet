package io.github.itsusinn.extension.org.lwjgl.components

import io.github.itsusinn.extension.org.lwjgl.unit.Coordinate
import kotlin.reflect.full.isSubclassOf

/**
 * GameObject是游戏场景中真实存在的，而且有位置的一个物件
 * Component附属于GameObject，控制GameObject的各种属性
 * GameObject是由Component组合成的，Component的生命周期和GameObject息息相关。
 * 调用此GameObject的Destroy方法，它的子对象和对应的所有Component都会被销毁，但也可以一次只销毁一个Component
 */
class GameObject(
    val name: String = "Nameless game object",
    val modelTransformation: ModelTransformation = ModelTransformation(),
    val shape: Coordinate = Coordinate()
) {
    val components = ArrayList<IComponent>()

    fun components(adder: GameObject.() -> Unit): GameObject {
        adder()
        return this
    }
    operator fun IComponent.unaryPlus() = components.add(this)

    fun update(dt: Float) {
        for (component in components) {
            component.update(dt)
        }
    }
    fun init() { }
    fun start() { }
    fun destroy() { }

    inline fun <reified T> getComponent(): T? where T : IComponent {
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
