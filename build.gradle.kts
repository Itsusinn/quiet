@file:Suppress("INACCESSIBLE_TYPE", "DEPRECATION")
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version ("1.4.30-RC")
    id("com.github.johnrengelman.shadow") version ("5.2.0")
    id("net.mamoe.kotlin-jvm-blocking-bridge") version "1.8.0"
}

val lwjglVersion = "3.2.3"
val jomlVersion = "1.10.0"
val jacksonVersion = "2.11.3"
val log4jVersion = "2.14.0"

application {
    mainClassName = "io.github.itsusinn.quiet.AppKt"
}

val lwjglNatives = when (OperatingSystem.current()) {
    OperatingSystem.LINUX -> "natives-linux"
    OperatingSystem.MAC_OS -> "natives-macos"
    OperatingSystem.WINDOWS -> {
        if (System.getProperty("os.arch").contains("64"))
            "natives-windows"
        else
            "natives-windows-x86"
    }
    else -> {
        throw Error("""Unrecognized or unsupported Operating system. Please set "lwjglNatives" manually""")
    }
}

val skijaPlatform = when (OperatingSystem.current()) {
    OperatingSystem.LINUX -> "linux"
    OperatingSystem.MAC_OS -> "macos"
    OperatingSystem.WINDOWS -> "windows"
    else -> {
        throw Error("Unrecognized or unsupported Operating system. Please set skijaPlatform manually")
    }
}

group = "io.github.itsusinn.quiet"
version = "0.1.0-rc1"

val skijaVersion = "0.89.0"
val vertxVersion = "4.0.0"

repositories {
    jcenter()
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://packages.jetbrains.team/maven/p/skija/maven")
    maven("https://jitpack.io")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
    jvmTarget = "11"
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.4.2")

    api("org.jetbrains.skija:skija-$skijaPlatform:${skijaVersion }")

    implementation("io.vertx:vertx-core:$vertxVersion")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    // logging
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")

    // implementation("com.github.kotlin-graphics:assimp:4.0")
    for (it in arrayOf("gl", "glfw", "core")) {
        implementation("com.github.kotlin-graphics.imgui:$it:1.79")
    }
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-jemalloc")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nfd")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)
    implementation("com.squareup.okio:okio:2.10.0")
}
