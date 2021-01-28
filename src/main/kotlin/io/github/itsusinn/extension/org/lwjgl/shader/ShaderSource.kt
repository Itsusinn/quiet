package io.github.itsusinn.extension.org.lwjgl.shader

import java.io.File
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

class ShaderSource(val filePath:String) {
   private val file = File(filePath)
   private val mixedSource = file.readLines()

   private val marker = ArrayList<Pair<Int,String>>().apply {
      for ((index,value) in mixedSource.withIndex()){
         if (value.startsWith("#type")) add(index to value)
      }
      add(mixedSource.size to "end")
   }

   fun parse(type:String):String{

      val nowIndex = marker.find { it.second == "#type $type" }
         ?: throw IllegalArgumentException("Don't exist $type source")
      val nextIndex = marker[ marker.indexOf(nowIndex) +1 ]
      val sourceBuilder = StringBuilder()
      for ((index,value) in mixedSource.withIndex()){
         if (index > nowIndex.first && index < nextIndex.first){
            sourceBuilder.appendLine(value)
         }
      }
      return sourceBuilder.toString()
   }
}