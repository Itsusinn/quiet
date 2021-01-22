package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.scene.Scene
import mu.KotlinLogging
import org.lwjgl.BufferUtils
import org.lwjgl.BufferUtils.*
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
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

   private val vertxArray =
      floatArrayOf(
      //position           // color
       0.5f, -0.5f,0.0f,     1.0f,0.0f,0.0f,1.0f,  //bottom right 0
      -0.5f,  0.5f,0.0f,     0.0f,1.0f,0.0f,1.0f,  //top left     1
       0.5f,  0.5f,0.0f,     0.0f,0.0f,1.0f,1.0f,  //top right    2
      -0.5f, -0.5f,0.0f,     1.0f,1.0f,0.0f,1.0f,  //bottom left  3
      )
   private val elementArray =
      intArrayOf(
         /*
               *-1     *-3

               *-2     *-0
          */

         1,0,3, //top right triangle
         1,2,0, //bottom left triangle

      )

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
      val vertexBuffer = createFloatBuffer(vertxArray.size).put(vertxArray).flip()

      //create vbo upload the vertex buffer

      vboID = glGenBuffers().apply {
         glBindBuffer(GL_ARRAY_BUFFER,this)
      }
      GL15.glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW)

      //create indices and upload

      val elementBuffer = createIntBuffer(elementArray.size).put(elementArray).flip()




   }

   override fun update(dt: Float) {

   }
}