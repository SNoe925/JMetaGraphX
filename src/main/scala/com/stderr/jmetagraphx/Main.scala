package com.stderr.jmetagraphx

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._

import org.apache.log4j.{Logger, Level}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Main {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("JMetaGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    ASMClassVisitor.main(args)
    val vertexRDD = ClassVertex.toRDD(sc)
    val edgeRDD = MethodCall.toRDD(sc)
    val graph = Graph(vertexRDD, edgeRDD)
    graph.triplets.foreach(format)
  }

  def format(t:EdgeTriplet[ClassVertex, MethodCall]): Unit = {
    println(t.srcAttr.name + " calls " + t.dstAttr.name
      + "." + t.attr.name + "(" + t.attr.desc + ")")
  }
}

case class ClassVertex(id: VertexId, name: String) {
  def toPair = (id, this)
}

object ClassVertex {
  var classVertexMap: mutable.Map[String, (VertexId, ClassVertex)] = mutable.HashMap()
  def getOrElseUpdate(name: String) =
    classVertexMap.getOrElseUpdate(name, new ClassVertex(classVertexMap.size + 1, name).toPair)
  def toRDD(sc: SparkContext) = sc.parallelize(classVertexMap.values.toSeq)
}

case class MethodCall(name: String, desc: String)

object MethodCall {
  val calls = ListBuffer[Edge[MethodCall]]()
  def toRDD(sc: SparkContext) = sc.parallelize(calls)
  def addMethodCall(caller: ClassVertex, owner: ClassVertex, method: MethodCall) = {
    val edge = Edge(caller.id, owner.id, method)
    calls += edge
  }
}
