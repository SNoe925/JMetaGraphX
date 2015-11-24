package com.stderr.jmetagraphx

import org.scalatest.FunSuite

class ASMClassVisitorSuite extends FunSuite {

  test("a test") {
    val in = classOf[String].getResourceAsStream("/java/lang/String.class")
    ASMClassVisitor.visit(in)
    val methodCalls = MethodCall.toSeq
    assert(methodCalls.nonEmpty)
  }

}