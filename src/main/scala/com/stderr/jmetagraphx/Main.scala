package com.stderr.jmetagraphx

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._

object Main {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("JMetaGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    val in = classOf[ASMClassVisitor].getResourceAsStream("/java/lang/String.class")
    ASMClassVisitor.visit(in)
    val vertexRDD = sc.parallelize(ClassVertex.toSeq)
    val edgeRDD = sc.parallelize(MethodCall.toSeq)
    val graph = Graph(vertexRDD, edgeRDD)
    graph.triplets.foreach(format)
  }

  def format(t:EdgeTriplet[ClassVertex, MethodCall]): Unit = {
    println(t.srcAttr.name + " calls " + t.dstAttr.name
      + "." + t.attr.name + "(" + t.attr.desc + ")")
  }
}

