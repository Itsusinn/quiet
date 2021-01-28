package io.github.itsusinn.extension.org.lwjgl.camera

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class Camera(
   val position:Vector2f
) {

   val projectionMatrix = Matrix4f().apply {
      identity()
      ortho(0f,32f * 40f,0f,32f * 21f,0f,100f)
   }

  private val viewMatrix = Matrix4f()

   fun getViewMatrix() = viewMatrix.apply {
      val cameraCenter = Vector3f(0f,0f,-1f)
      val cameraUp = Vector3f(0f,1f,0f)
      val eyePosition = Vector3f(position.x,position.y,20f)
      identity()
      lookAt(
         eyePosition,
         cameraCenter.add(position.x,position.y,0f),
         cameraUp
      )!!
   }
}