package com.example.leonapplication.asmdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.leonapplication.extension.list
import com.example.leonapplication.extension.text

class AsmDemoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val testClass = TestClass()

    val msg = with(StringBuilder()) {
      append(testClass.print())
      append("\n")
      append("print: ${testClass.print}")
    }.toString()

    list {
      text(text = msg) {}
    }
  }
}