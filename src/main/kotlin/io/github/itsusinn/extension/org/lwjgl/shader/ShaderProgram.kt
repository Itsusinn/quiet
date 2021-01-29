package io.github.itsusinn.extension.org.lwjgl.shader

import io.github.itsusinn.extension.org.lwjgl.memory.stack
import io.github.itsusinn.extension.org.lwjgl.texture.Texture
import mu.KotlinLogging
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack
import java.util.*

private val logger by lazy { KotlinLogging.logger {  } }

class ShaderProgram {
   private val program by lazy { glCreateProgram() }

   fun use() = glUseProgram(program)
   fun detach() = glUseProgram(0)

   fun attachShader(shader:Shader){
      glAttachShader(program,shader.shaderID)
   }
   fun link(){
      glLinkProgram(program)
      checkLink()
   }

   fun uploadMatrix4(varName:String,mat4:Matrix4f){
      val location = glGetUniformLocation(program,varName)
      stack {
         val matBuffer = mallocFloat(16)
         mat4.get(matBuffer)
         glUniformMatrix4fv(location,false,matBuffer)
      }
   }
   fun uploadInt(varName:String,int: Int){
      val location = glGetUniformLocation(program,varName)
      use()
      glUniform1i(location,int)
   }
   fun uploadFloat(varName:String,float:Float){
      val location = glGetUniformLocation(program,varName)
      use()
      glUniform1f(location,float)
   }
   fun uploadTexture(varName:String,slot:Int){
      val location = glGetUniformLocation(program,varName)
      use()
      glUniform1i(location,slot)
   }


   private fun checkLink(){
      val success = glGetProgrami(program, GL_LINK_STATUS)
      if (success == GL_FALSE){
         val len = glGetProgrami(program, GL_INFO_LOG_LENGTH)
         logger.error { "Error happened in linking shaders" }
         logger.error { glGetProgramInfoLog(program,len) }
         throw IllegalStateException()
      }
   }
}