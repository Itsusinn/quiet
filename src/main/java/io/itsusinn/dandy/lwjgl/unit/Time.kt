@file:Suppress("NOTHING_TO_INLINE")
package io.itsusinn.dandy.lwjgl.unit

/**
 * Time
 * Time类是Unity中的一个全局变量，它记载了和游戏相关的时间，帧数等数据
 * Time类包含一个非常重要的变量叫deltaTime.这个变量包含从上次调用Update
 * 或FixedUpdate到现在的时间(根据你是放在Update函数还是FixedUpdate函数中)(Update每帧调用一次)
 */
class Time

inline fun now() = ((System.nanoTime() - start) * 1E-9).toFloat()

val start = System.nanoTime()
