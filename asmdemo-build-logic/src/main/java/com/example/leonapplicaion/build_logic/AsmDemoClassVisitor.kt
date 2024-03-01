package com.example.leonapplicaion.build_logic

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class AsmDemoClassVisitor(api: Int, next: ClassVisitor) : ClassVisitor(api, next) {

  override fun visitMethod(
    access: Int,
    name: String?,
    descriptor: String?,
    signature: String?,
    exceptions: Array<out String>?,
  ): MethodVisitor {
    val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
    return ReturnMethodMethodVisitor(api, mv, access, name, descriptor)
  }

  private inner class ReturnMethodMethodVisitor(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?,
  ) : AdviceAdapter(api, mv, access, name, descriptor) {

    override fun visitCode() {
      mv.visitLdcInsn("hook hook")
      mv.visitInsn(Opcodes.ARETURN)
    }
  }
}