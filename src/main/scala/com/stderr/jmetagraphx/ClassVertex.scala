package com.stderr.jmetagraphx

import org.apache.spark.graphx._

import scala.collection.mutable

case class ClassVertex(id: VertexId, name: String) {
  def toPair = (id, this)
}

object ClassVertex {
  private val classVertexMap: mutable.Map[String, (VertexId, ClassVertex)] = mutable.HashMap()

  def getOrElseUpdate(name: String) =
    classVertexMap.getOrElseUpdate(name, new ClassVertex(classVertexMap.size + 1, name).toPair)

  def toSeq:Seq[Pair[VertexId, ClassVertex]] = classVertexMap.values.toSeq
}
