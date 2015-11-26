package com.stderr.jmetagraphx

import org.apache.spark.graphx._

import scala.collection.mutable

case class ClassVertex(id: VertexId, name: String) {
  def toPair = (id, this)
}

object ClassVertex {
  private val classVertexMap: mutable.Map[String, (VertexId, ClassVertex)] = mutable.HashMap()

  def getOrElseUpdate(name: String, jarId:Integer = 0) = {
    val id: VertexId = jarId << 48 + classVertexMap.size + 1
    classVertexMap.getOrElseUpdate(name.intern(), new ClassVertex(id, name).toPair)
  }

  def toSeq:Seq[Pair[VertexId, ClassVertex]] = classVertexMap.values.toSeq
}
