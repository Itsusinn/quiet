@file:Suppress("NOTHING_TO_INLINE")
package io.github.itsusinn.quiet

import io.github.itsusinn.extension.org.lwjgl.camera.Camera
import io.github.itsusinn.extension.org.lwjgl.memory.buf
import io.github.itsusinn.extension.org.lwjgl.memory.floatDirectArrayOf
import io.github.itsusinn.extension.org.lwjgl.scene.Scene
import io.github.itsusinn.extension.org.lwjgl.shader.FragmentShader
import io.github.itsusinn.extension.org.lwjgl.shader.ShaderProgram
import io.github.itsusinn.extension.org.lwjgl.shader.ShaderSource
import io.github.itsusinn.extension.org.lwjgl.shader.VertexShader
import io.github.itsusinn.extension.org.lwjgl.texture.Texture
import mu.KotlinLogging
import org.joml.Vector2f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

private val logger = KotlinLogging.logger {  }


class LevelEditorScene: Scene() {

   private val source = ShaderSource("assets/shaders/default.glsl")

   private val vertexShader = VertexShader.create(source)
   private val fragmentShader = FragmentShader.create(source)

   val shaderProgram = ShaderProgram()
   val camera = Camera(Vector2f(0f,0f))

   private val vertxArray = floatArrayOf(
      //position        // color       //uv coordinates
      0f,   500f, 0f,   1f,1f,0f,1f,   0f, 0f,  //top left     0
      500f, 500f, 0f,   1f,1f,0f,1f,   1f, 0f,  //top right    1
      0f,   0f,   0f,   1f,0f,1f,1f,   0f, 1f,  //bottom left  2
      500f, 0f,   0f,   1f,0f,0f,1f,   1f, 1f,  //bottom right 3

      /**
       *0     1
       *
       *2     3
       */
   )


   private val elementArray = intArrayOf(
      1,2,0, //top right triangle
      1,2,3, //bottom left triangle
   )

   private val vaoID by lazy { glGenVertexArrays() }

   //使用glGenBuffers函数生成一个VBO对象并返回一个缓冲ID
   private val vboID by lazy { glGenBuffers() }

   private val eboID by lazy { glGenBuffers() }

   private val texture = Texture("assets/images/test.jpg")

   override fun init() {

      /**
       * compile and link shaders
       */

      vertexShader.compile()
      fragmentShader.compile()

      //link shaders
      shaderProgram.attachShader(vertexShader)
      shaderProgram.attachShader(fragmentShader)

      shaderProgram.link()

      /**
       * generate VAO,VBO,and EBO buffer objects and send to gpu
       * 顶点数组对象：Vertex Array Object，VAO
       * 顶点缓冲对象：Vertex Buffer Object，VBO
       * 索引缓冲对象：Element Buffer Object，EBO或Index Buffer Object，IBO
       */

      glBindVertexArray(vaoID)

      //使用glBindBuffer函数把创建的缓冲绑定到GL_ARRAY_BUFFER目标上
      glBindBuffer(GL_ARRAY_BUFFER,vboID)
      //把顶点数据储存在显卡的内存中，用VBO这个顶点缓冲对象管理
      glBufferData(GL_ARRAY_BUFFER ,vertxArray.buf(), GL_STATIC_DRAW)

      //create indices and upload
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID)
      glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementArray.buf(), GL_STATIC_DRAW)

      //Add vertex attribute pointers
      //position 占三个浮点数
      val positionSize = 3
      //color 占四个浮点数
      val colorSize = 4
      //uv coordinate
      val uvSize = 2


      val vertexSizeBytes = (positionSize+colorSize+uvSize) * Float.SIZE_BYTES



      glVertexAttribPointer(
         0,
         positionSize,
         GL_FLOAT,
         false,
         vertexSizeBytes,
         0L
      )
      glEnableVertexAttribArray(0)

      glVertexAttribPointer(
         1,
         colorSize,
         GL_FLOAT,
         false,
         vertexSizeBytes,
         (positionSize * Float.SIZE_BYTES).toLong()
      )
      glEnableVertexAttribArray(1)

      glVertexAttribPointer(
         2,
         uvSize, //size
         GL_FLOAT, //type
         false,
         vertexSizeBytes,
         ((positionSize+colorSize) * Float.SIZE_BYTES).toLong()
      )
      glEnableVertexAttribArray(2)
   }

   override fun update(dt: Float) {
      //bind shader program
      shaderProgram.use()
      shaderProgram.uploadTexture("tex",0)
      glActiveTexture(GL_TEXTURE0)
      texture.bind()

      shaderProgram.uploadMatrix4("uProjection",camera.projectionMatrix)
      shaderProgram.uploadMatrix4("uView",camera.getViewMatrix())
      //bind the VAO that we are using,实际上是绑定上下文
      glBindVertexArray(vaoID)

      //enable the vertex attribute pointer
      glEnableVertexAttribArray(0)
      glEnableVertexAttribArray(1)
      glEnableVertexAttribArray(2)
      /**
       * 通过glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)函数配置OpenGL如何绘制图元。
       * 第一个参数表示我们打算将其应用到所有的三角形的正面和背面，第二个参数告诉我们用线来绘制。
       * 之后的绘制调用会一直以线框模式绘制三角形，
       * 直到我们用glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)将其设置回默认模式。
       */
      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)

      /**
       * 为了让OpenGL知道我们的坐标和颜色值构成的到底是什么，OpenGL需要你去指定这些数据所表示的渲染类型。
       * 我们是希望把这些数据渲染成一系列的点？一系列的三角形？还是仅仅是一个长长的线？
       * 做出的这些提示叫做图元(Primitive)，任何一个绘制指令的调用都将把图元传递给OpenGL。
       * 这是其中的几个：GL_POINTS、GL_TRIANGLES、GL_LINE_STRIP。
       */

      glDrawElements(GL_TRIANGLES,elementArray.size, GL_UNSIGNED_INT,0)

      //unbind everything
      glDisableVertexAttribArray(0)
      glDisableVertexAttribArray(1)
      glDisableVertexAttribArray(2)

      glBindVertexArray(0)
      shaderProgram.detach()
   }
}