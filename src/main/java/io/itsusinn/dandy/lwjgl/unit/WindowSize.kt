package io.github.itsusinn.quiet.extension.org.lwjgl.unit

public data class WindowSize(
    public val width: Int,
    public val height: Int
)

public infix fun Int.with(that: Int): WindowSize = WindowSize(this, that)
