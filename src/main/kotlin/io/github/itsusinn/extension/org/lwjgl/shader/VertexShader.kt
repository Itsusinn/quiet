package io.github.itsusinn.extension.org.lwjgl.shader

import mu.KotlinLogging
import org.lwjgl.opengl.GL30.*
import java.lang.IllegalStateException

private val logger = KotlinLogging.logger {  }

class VertexShader(
   val source:String
):Shader {
   override val shaderID:Int by lazy {
      glCreateShader(GL_VERTEX_SHADER).also { id ->
         //pass the source code to gpu
         glShaderSource(id,source)
      }
   }
   override fun compile(){
      glCompileShader(shaderID)
      //check error in compilation
      checkCompile { "Error happened in vertex shader compilation" }
   }

   companion object{
      fun create(source:ShaderSource):VertexShader =
         VertexShader(source.parse("vertex"))
   }
}