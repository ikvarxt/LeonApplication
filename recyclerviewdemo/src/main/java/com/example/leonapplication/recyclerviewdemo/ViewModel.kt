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

  val isEditMode get() = _uiState.value is UiState.Edit

  fun request(size: Int = Constants.PAGE_SIZE) = viewModelScope.launch {
    _uiState.emit(UiState.Loading)
    try {
      val items = getData(startIndex, size)
      startIndex += items.size
      _items.addAll(items)
      _uiState.emit(UiState.Success(produceUiListItem(_items)))
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

  fun loadMore() = viewModelScope.launch {
    try {
      val items = getData(startIndex, Constants.PAGE_SIZE)
      startIndex += items.size
      _items.addAll(items)
      _uiState.emit(UiState.Success(produceUiListItem(_items)))
    } catch (e: Exception) {
      _uiState.emit(UiState.Error(Constants.Error.LoadingFailed))
    }
  }

  fun enterEditMode() = viewModelScope.launch {
    val list = produceUiListItem(items).map {
      it.copy(isEditMode = true)
    }
    _uiState.emit(UiState.Edit(list))
  }

  fun exitEditMode() = viewModelScope.launch {
    val list = produceUiListItem(items).map {
      it.copy(isEditMode = false, isChecked = false)
    }
    _uiState.emit(UiState.Success(list))
  }

  private suspend fun produceUiListItem(data: List<Item>): List<ListItem> = withContext(Dispatchers.Default) {
    return@withContext buildList {
      add(ListItem(Constants.ViewType.Header, text = "List Header"))
      for (item in data) {
        add(ListItem(Constants.ViewType.Card, item))
      }
      if (data.size <= 30) {
        add(ListItem(Constants.ViewType.LoadMore))
      } else {
        add(ListItem(Constants.ViewType.Footer, text = "Footer here"))
      }
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

  fun onChecked(listItem: ListItem, checked: Boolean) = viewModelScope.launch {
    val list = produceUiListItem(items)
      .map { item ->
        if (item == listItem) {
          item.copy(isChecked = checked, isEditMode = true)
        } else item.copy(isEditMode = true)
      }
    _uiState.emit(UiState.Edit(list))
  }

}

sealed class UiState {
  data object Loading : UiState()
  data class Error(val error: Constants.Error) : UiState()
  data class Success(val items: List<ListItem>) : UiState()
  data class Edit(val items: List<ListItem>) : UiState()
}
