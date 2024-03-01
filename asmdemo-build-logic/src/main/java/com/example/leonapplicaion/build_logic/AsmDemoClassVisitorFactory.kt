package com.example.leonapplicaion.build_logic

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor

abstract class AsmDemoClassVisitorFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {

  override fun isInstrumentable(classData: ClassData): Boolean {
    return classData.className == "com.example.leonapplication.asmdemo.TestClass"
  }

  override fun createClassVisitor(
    classContext: ClassContext,
    nextClassVisitor: ClassVisitor,
  ): ClassVisitor {
    val api = instrumentationContext.apiVersion.get()
    return AsmDemoClassVisitor(api, nextClassVisitor)
  }
}