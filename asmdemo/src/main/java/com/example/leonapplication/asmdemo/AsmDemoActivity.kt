package com.example.leonapplication.asmdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.leonapplication.asmdemo.databinding.ActivityAsmDemoBinding

class AsmDemoActivity : AppCompatActivity() {

  private lateinit var binding: ActivityAsmDemoBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityAsmDemoBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.text.text = TestClass().print()
  }
}