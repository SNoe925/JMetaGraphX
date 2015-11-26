package com.stderr.jmetagraphx

import java.io.{FileInputStream, File, InputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import org.apache.log4j.Logger
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

object ASMClassVisitor {
  val logger = Logger.getLogger(ASMClassVisitor.getClass)
  val isDebug = logger.isDebugEnabled

  def visit(allJars: Array[File]):Unit = allJars.zip(Stream from 0).foreach(p => visit(p._1, p._2))

  def visit(jarFile:File, jarId: Int): Unit = {
    logger.info(s"visit jar ${jarFile.getPath} as id=$jarId")
    val zip:ZipInputStream = new ZipInputStream(new FileInputStream(jarFile))
    try {
      var zipEntry: ZipEntry = zip.getNextEntry
      while (zipEntry != null) {
        if (zipEntry.getName.endsWith(".class")) {
          if (isDebug)
            logger.debug(s"visit class ${zipEntry.getName}")
          visit(zip)
        }
        zipEntry = zip.getNextEntry
      }
    } finally {
      zip.close()
    }
  }

  def visit(in: InputStream): Unit = {
    val classReader = new ClassReader(in)
    val classVisitor = new ASMClassVisitor
    classReader.accept(classVisitor, 0)
  }
}

class ASMClassVisitor(implicit val jarId:Integer = 0) extends ClassVisitor(Opcodes.ASM4)
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
    new ASMMethodVisitor(className, jarId)
  }
}