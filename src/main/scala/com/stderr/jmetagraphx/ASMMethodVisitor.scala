package com.stderr.jmetagraphx

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM4

/*
 * Visit method body.
 */
case class ASMMethodVisitor(var caller: String, jarId:Integer = 0) extends MethodVisitor(ASM4) {

  val callerVertex = ClassVertex.getOrElseUpdate(caller, jarId)._2

  /*
   * Visit a method call.
   */
  override def visitMethodInsn(opcode: Int, owner: String, name: String,
      desc: String, itf: Boolean) {
    val ownerVertex = ClassVertex.getOrElseUpdate(owner, jarId)._2
    MethodCall.addMethodCall(callerVertex, ownerVertex, MethodCall(name, desc))
  }
    
}