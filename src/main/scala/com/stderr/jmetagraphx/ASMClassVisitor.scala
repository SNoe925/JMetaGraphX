package com.stderr.jmetagraphx

import java.io.InputStream

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

object ASMClassVisitor {
  def visit(in: InputStream): Unit = {
    val classReader = new ClassReader(in)
    val classVisitor = new ASMClassVisitor
    classReader.accept(classVisitor, 0)
  }
}

class ASMClassVisitor extends ClassVisitor(Opcodes.ASM4)
{
  var className: String = ""
  
  override def visit(version: Int, access: Int, name: String,
    signature: String, superName: String, interfaces: Array[String]) {
    className = name
    super.visit(version, access, name, signature, superName, interfaces)
  }

  override def visitEnd() = { super.visitEnd() }

  override def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = {
    super.visitAnnotation(desc, visible)
  }
  
  override def visitMethod(access: Int, name: String,
    desc: String, signature: String, exceptions: Array[String]): ASMMethodVisitor = {
    new ASMMethodVisitor(className)
  }
}