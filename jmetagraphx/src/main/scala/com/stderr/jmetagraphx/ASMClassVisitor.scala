package com.stderr.jmetagraphx;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

object ASMClassVisitor {
  def main(args: Array[String]): Unit = {
    val in = classOf[ASMClassVisitor].getResourceAsStream("/java/lang/String.class")
    val classReader = new ClassReader(in)
    val classVisitor = new ASMClassVisitor
    classReader.accept(classVisitor, 0)
  }
}

class ASMClassVisitor extends ClassVisitor(Opcodes.ASM4)
{
  override def visit(version: Int, access: Int, name: String,
    signature: String, superName: String, interfaces: Array[String]) {
    println("Visiting class: " + name)
    println("Class Major Version: " + version)
    println("Super class: " + superName)
    super.visit(version, access, name, signature, superName, interfaces)
  }

  override def visitEnd() = { super.visitEnd() }

  override def visitAnnotation(desc: String, visible: Boolean): AnnotationVisitor = {
    super.visitAnnotation(desc, visible)
  }
  
  override def visitMethod(access: Int, name: String,
    desc: String, signature: String, exceptions: Array[String]): MethodVisitor = {
    println("Method: " + name + " " + desc)
    super.visitMethod(access, name, desc, signature, exceptions)
  }
}