package io.github.itsusinn.extension.org.lwjgl

import org.lwjgl.glfw.GLFW
import kotlin.collections.ArrayList

//save reference of some objects to escape from the gc
private val GC_Root = ArrayList<Any>()

object GlfwManager{

}
object GlfwConfiguration{
   fun init(){

   }
   fun setDefault(){
      GLFW.glfwDefaultWindowHints()
   }
}