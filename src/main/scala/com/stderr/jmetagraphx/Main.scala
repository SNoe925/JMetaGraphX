package com.stderr.jmetagraphx

import java.io.File

import org.apache.log4j.Logger
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

object Main {

  val logger = Logger.getLogger(Main.getClass)

  val homeDirectory = System.getProperty("user.home")
  val vertexFilePath: String = s"$homeDirectory/vertex.dat"
  val edgeFilePath: String = s"$homeDirectory/edge.dat"

  def main(args: Array[String]) {
    val mvnRepo = s"$homeDirectory/.m2/repository"
    val directoryToScan = if (args.isEmpty) new File(mvnRepo) else new File(args(0))
    val allJars = recursiveListFiles(directoryToScan).filter(_.getName.endsWith(".jar"))
    info(s"scanning ${allJars.length} jars")
    ASMClassVisitor.visit(allJars)

    val conf = new SparkConf().setAppName("JMetaGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val vertexRDD = sc.parallelize(ClassVertex.toSeq)
    vertexRDD.saveAsObjectFile(vertexFilePath)

    val edgeRDD = sc.parallelize(MethodCall.toSeq)
    edgeRDD.saveAsObjectFile(edgeFilePath)

//    run(sc)
  }

  def run(sc:SparkContext) = {
    val vertexRDD = sc.objectFile[(VertexId, ClassVertex)](vertexFilePath)
    val edgeRDD = sc.objectFile[Edge[MethodCall]](edgeFilePath)
    val graph = Graph(vertexRDD, edgeRDD)

    graph.triplets.foreach(format)
  }

  def info(msg: String): Unit = {
    logger.info(s"###\n$msg\n###")
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

