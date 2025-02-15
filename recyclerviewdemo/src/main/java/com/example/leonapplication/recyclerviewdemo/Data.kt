package com.example.leonapplication.recyclerviewdemo

data class Item(
  val id: Int,
  val name: String,
  val imgUrl: String,
) {

  companion object {

    private var sId = 0

    fun new(i: Int? = null): Item {
      val id = sId++
      val n = i ?: ""
      val name = "Hello $id $n"
      val imgUrl = "https://picsum.photos/200/300?random=$id"
      return Item(id, name, imgUrl)
    }
  }
}
