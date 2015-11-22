package com.stderr.jmetagraphx

import org.apache.spark.graphx.Edge

import scala.collection.mutable.ListBuffer

case class MethodCall(name: String, desc: String)

object MethodCall {
  private val calls = ListBuffer[Edge[MethodCall]]()

  def toSeq:Seq[Edge[MethodCall]] = calls.toSeq

  def addMethodCall(caller: ClassVertex, owner: ClassVertex, method: MethodCall) = {
    val edge = Edge(caller.id, owner.id, method)
    calls += edge
  }
}
