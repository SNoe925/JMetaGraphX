package com.stderr.jmetagraphx

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM4

/*
 * Visit method body.
 */
case class ASMMethodVisitor(var caller: String) extends MethodVisitor(ASM4) {
  /*
   * Visit a method call.
   */
  override def visitMethodInsn(opcode: Int, owner: String, name: String,
      desc: String, itf: Boolean) {
    val callerVertex = ClassVertex.getOrElseUpdate(caller)._2
    val ownerVertex = ClassVertex.getOrElseUpdate(owner)._2
    MethodCall.addMethodCall(callerVertex, ownerVertex, MethodCall(name, desc))
  }
    
}