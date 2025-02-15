package com.example.leonapplication.recyclerviewdemo

object Constants {
  const val FIRST_PAGE_SIZE = 6
  const val PAGE_SIZE = 9

  enum class ViewType(val type: Int) {
    Card(0),
    Header(1),
    LoadMore(2),
    Footer(3),
  }
  enum class Error(code: Int) {
    NoInternet(0),
    NotLogin(1),
    LoadingFailed(3),
    InternalError(4),
  }
}