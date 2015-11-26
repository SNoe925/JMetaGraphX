package com.stderr.jmetagraphx

import java.io.File

import org.apache.log4j.{Level, Logger, BasicConfigurator}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.sql.SQLContext

import org.apache.commons.io.FileUtils

object Main {

  val logger = Logger.getLogger(Main.getClass)

  val homeDirectory = System.getProperty("user.home")
  val vertexFilePath: String = s"$homeDirectory/vertex.dat"
  val edgeFilePath: String = s"$homeDirectory/edge.dat"

  def main(args: Array[String]) {
    // Quiet the defaults from spark. Use INFO for development
    BasicConfigurator.configure()
    Logger.getRootLogger.setLevel(Level.ERROR)

    cleanupFiles

    // Extract the method call information from jar files
    scanJars(args)

    // Create Spark
    val conf = new SparkConf().setAppName("JMetaGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    // Create RDDs from the scanned classes
    val vertexRDD = sc.parallelize(ClassVertex.toSeq)
    val edgeRDD = sc.parallelize(MethodCall.toSeq)

    // Save the RDDs for use int the spark shell
    vertexRDD.saveAsObjectFile(vertexFilePath)
    edgeRDD.saveAsObjectFile(edgeFilePath)

    // Analysis
    run(sc)
  }

  def scanJars(args: Array[String]) = {
    val mvnRepo = s"$homeDirectory/.m2/repository"
    val directoryToScan = if (args.isEmpty) new File(mvnRepo) else new File(args(0))
    val allJars = recursiveListFiles(directoryToScan).filter(_.getName.endsWith(".jar"))
    info(s"scanning ${allJars.length} jars")
    ASMClassVisitor.visit(allJars)
  }

  def run(sc:SparkContext) = {
    val vertexRDD = sc.objectFile[(VertexId, ClassVertex)](vertexFilePath)
    val edgeRDD = sc.objectFile[Edge[MethodCall]](edgeFilePath)
    val graph = Graph(vertexRDD, edgeRDD)

    // Create an inverted and swapped RDD of the indegree of the map
    // to create called stats
    val inDegreeVertexId = graph.inDegrees.map(t => (t._2 * -1, t._1))
    val topN = 10
    val topCalled = inDegreeVertexId.takeOrdered(topN).map(_._2)
    println(s"\n\n\nTop $topN called classes")
    vertexRDD.filter(x => topCalled.contains(x._1)).map(_._2.name).foreach(println)
    println("\n\n")
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

  def cleanupFiles = {
    val paths = List(vertexFilePath, edgeFilePath)
    for (fileName <- paths;
         file <- Some(new File(fileName)) if (file.exists());
         deleted <- Some(FileUtils.deleteDirectory(file))) yield deleted
  }
}

