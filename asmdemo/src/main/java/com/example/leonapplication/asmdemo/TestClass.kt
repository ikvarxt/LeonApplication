package com.example.leonapplication.asmdemo

class TestClass {

  var print = "TestClass"

  private fun modifyPrint() {
    print = "test change print"
  }

  fun print(): String {
    modifyPrint() // not reached
    return print
  }
}