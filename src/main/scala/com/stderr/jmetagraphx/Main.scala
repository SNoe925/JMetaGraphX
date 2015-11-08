package com.stderr.jmetagraphx

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Main {
  def main(args: Array[String]) {
    val logFile = "YOUR_SPARK_HOME/README.md" // Should be some file on your system
    val conf = new SparkConf().setAppName("JMetaGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    // TODO
    ASMClassVisitor.main(args)
    sc.stop()
  }
}