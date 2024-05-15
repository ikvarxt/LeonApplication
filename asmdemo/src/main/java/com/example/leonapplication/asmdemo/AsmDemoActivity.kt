package com.example.leonapplication.asmdemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.leonapplication.asmdemo.databinding.ActivityAsmDemoBinding

class AsmDemoActivity : AppCompatActivity() {

  private lateinit var binding: ActivityAsmDemoBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    binding = ActivityAsmDemoBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val testClass = TestClass()
    binding.text.text = with(StringBuilder()) {
      append(testClass.print())
      append("\n")
      append("print: ${testClass.print}")
    }
  }
}