package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.NullPointer
import io.github.itsusinn.extension.org.lwjgl.scene.Scene
import mu.KotlinLogging
import org.lwjgl.BufferUtils.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.properties.Delegates

private val logger = KotlinLogging.logger {  }

class LevelEditorScene: Scene() {

   private val vertexShaderSrc = """
      #version 330 core

      layout (location=0) in vec3 aPos;
      layout (location=1) in vec4 aColor;

      out vec4 fColor;

      void main (void) {
          fColor = aColor;  
          gl_Position = vec4(aPos,1.0);
      }
   """.trimIndent()
   private val fragmentShaderSrc = """
      #version 330 core

      in vec4 fColor;

      out vec4 color;

      void main(){
      	color = fColor;
      }
   """.trimIndent()

   private var vertexID by Delegates.notNull<Int>()
   private var fragmentID by Delegates.notNull<Int>()
   private var shaderProgram by Delegates.notNull<Int>()


   private val vertxArrayDirect = FloatBuffer.wrap(
      floatArrayOf(
         //position           // color
         0.5f, -0.5f,0.0f,     1.0f,0.0f,0.0f,1.0f,  //bottom right 0
         -0.5f,  0.5f,0.0f,     1.0f,1.0f,0.0f,1.0f,  //top left     1
         0.5f,  0.5f,0.0f,     0.0f,0.0f,1.0f,1.0f,  //top right    2
         -0.5f, -0.5f,0.0f,     1.0f,1.0f,0.0f,1.0f,  //bottom left  3
      )
   ).flip()
   private val elementArrayDirect = IntBuffer.wrap(
      intArrayOf(
         /*
               * 1     * 3

               * 2     * 0
          */

         1,0,3, //top right triangle
         1,2,0, //bottom left triangle

      )
   ).flip()


   private var vaoID by Delegates.notNull<Int>()
   private var vboID by Delegates.notNull<Int>()
   private var eboID by Delegates.notNull<Int>()

   override fun init() {

      /**
       * compile and link shaders
       */

      //first load and compile vertex shader
      vertexID = glCreateShader(GL_VERTEX_SHADER)
      //pass the source code to gpu
      glShaderSource(vertexID,vertexShaderSrc)
      glCompileShader(vertexID)
      //check error in compilation
      var success = glGetShaderi(vertexID, GL_COMPILE_STATUS)
      if (success == GL_FALSE){
         val len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH)
         logger.error { "Error happened in shader compilation" }
         logger.error { glGetShaderInfoLog(vertexID,len) }
         assert(false)
      }

      //first load and compile vertex shader
      fragmentID = glCreateShader(GL_FRAGMENT_SHADER)
      //pass the source code to gpu
      glShaderSource(fragmentID,fragmentShaderSrc)
      glCompileShader(fragmentID)
      //check error in compilation
      success = glGetShaderi(fragmentID, GL_COMPILE_STATUS)
      if (success == GL_FALSE){
         val len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH)
         logger.error { "Error happened in shader compilation" }
         logger.error { glGetShaderInfoLog(fragmentID,len) }
      }



      //link shaders and check for errors
      shaderProgram = glCreateProgram()
      glAttachShader(shaderProgram,vertexID)
      glAttachShader(shaderProgram,fragmentID)
      glLinkProgram(shaderProgram)

      //check for errors
      success = glGetProgrami(shaderProgram, GL_LINK_STATUS)
      if (success == GL_FALSE){
         val len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH)
         logger.error { "Error happened in linking shaders" }
         logger.error { glGetProgramInfoLog(fragmentID,len) }
         assert(false)
      }

      /**
       * generate VAO,VBO,and EBO buffer objects and send to gpu
       * VAO: vertex array object
       * VBO: vertx buffer object
       * EBO: elements buffer object
       */

      vaoID = glGenVertexArrays()
      glBindVertexArray(vaoID)

      //create a float buffers of vertices
      //使用glGenBuffers函数生成一个VBO对象并返回一个缓冲ID
      vboID = glGenBuffers()
      //使用glBindBuffer函数把新创建的缓冲绑定到GL_ARRAY_BUFFER目标上
      glBindBuffer(GL_ARRAY_BUFFER,vboID)
      glBufferData(GL_ARRAY_BUFFER,vertxArrayDirect.array(), GL_STATIC_DRAW)

      //create indices and upload
      eboID = glGenBuffers()
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID)
      glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementArrayDirect.array(), GL_STATIC_DRAW)

      //Add vertex attribute pointers

      val positionSize = 3
      val colorSize = 4
      val floatSizeByte = 4
      val vertexSizeBytes = (positionSize+colorSize) * floatSizeByte

      glVertexAttribPointer(0,positionSize, GL_FLOAT,false,vertexSizeBytes,0L)
      glEnableVertexAttribArray(0)

      glVertexAttribPointer(1,colorSize, GL_FLOAT,false,vertexSizeBytes,(positionSize * floatSizeByte).toLong())
      glEnableVertexAttribArray(1)

   }

   override fun update(dt: Float) {
      //bind shader program
      glUseProgram(shaderProgram)
      //bind the VAO that we are using
      glBindVertexArray(vaoID)


      //enable the vertex attribute pointer
      glEnableVertexAttribArray(0)
      glEnableVertexAttribArray(1)
      glDrawElements(GL_TRIANGLES,elementArrayDirect.capacity(), GL_UNSIGNED_INT,0)

      //unbind everything
      glDisableVertexAttribArray(0)
      glDisableVertexAttribArray(1)

      glBindVertexArray(0)
      glUseProgram(0)
   }
}