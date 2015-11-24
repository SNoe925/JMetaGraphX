package com.stderr.jmetagraphx

import java.io.File

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._

object Main {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("JMetaGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    val mvnRepo = System.getProperty("user.home") + "/.m2/repository"
    val directoryToScan = if (args.isEmpty) new File(mvnRepo) else new File(args(0))
    val allJars = recursiveListFiles(directoryToScan).filter(_.getName.endsWith(".jar"))
    ASMClassVisitor.visit(allJars)
    val vertexRDD = sc.parallelize(ClassVertex.toSeq)
    val edgeRDD = sc.parallelize(MethodCall.toSeq)
    val graph = Graph(vertexRDD, edgeRDD)
    graph.triplets.foreach(format)
  }

  def format(t:EdgeTriplet[ClassVertex, MethodCall]): Unit = {
    println(t.srcAttr.name + " calls " + t.dstAttr.name
      + "." + t.attr.name + "(" + t.attr.desc + ")")
  }

  def recursiveListFiles(f:File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
}

