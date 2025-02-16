package com.example.leonapplication.recyclerviewdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel : ViewModel() {

  private val _items = mutableListOf<Item>()
  val items: List<Item> = _items

  private var startIndex = 0

  private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
  val uiState: StateFlow<UiState> = _uiState

  fun request(size: Int = Constants.PAGE_SIZE) = viewModelScope.launch {
    _uiState.emit(UiState.Loading)
    try {
      val items = getData(startIndex, size)
      startIndex += items.size
      _items.addAll(items)
      _uiState.emit(UiState.Success(processingData(_items)))
    } catch (e: Exception) {
      _uiState.emit(UiState.Error(Constants.Error.LoadingFailed))
    }
  }

  fun requestFirstPage() = request(Constants.FIRST_PAGE_SIZE)

  fun refresh() {
    startIndex = 0
    _items.clear()
    request()
  }

  private fun processingData(data: List<Item>): List<ListItem> = buildList {
    add(ListItem(Constants.ViewType.Header))
    for (item in data) {
      add(ListItem(Constants.ViewType.Card, item))
    }
    if (data.size <= Constants.FIRST_PAGE_SIZE) {
      add(ListItem(Constants.ViewType.LoadMore))
    } else {
      add(ListItem(Constants.ViewType.Footer))
    }
  }

  private suspend fun getData(start: Int, size: Int) = withContext(Dispatchers.Default) {
    val items = buildList {
      repeat(size) { i ->
        delay(50)
        add(Item.new(start + i))
      }
    }
    return@withContext items
  }

}

sealed class UiState {
  data object Loading : UiState()
  data class Error(val error: Constants.Error) : UiState()
  data class Success(val items: List<ListItem>) : UiState()
}
