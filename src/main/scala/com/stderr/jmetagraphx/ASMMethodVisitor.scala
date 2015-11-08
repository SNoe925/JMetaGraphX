package com.stderr.jmetagraphx

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


/*
 * Visit method body.
 */
case class ASMMethodVisitor(var caller: String) extends MethodVisitor(Opcodes.ASM4) {
  /*
   * Visit a method call.
   */
  override def visitMethodInsn(opcode: Int, owner: String, name: String,
      desc: String, itf: Boolean) {
    println(s"${caller},${owner},${name}${desc}")
  }
    
}