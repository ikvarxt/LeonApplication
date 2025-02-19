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

  private val _selectedItems = mutableSetOf<Item>()
  val selectedItems: Set<Item> = _selectedItems

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

  fun requestFirstPage() {
    if (startIndex != 0) return
    request(Constants.FIRST_PAGE_SIZE)
  }

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
    val list = produceUiListItem(items, edit = true)
    _uiState.emit(UiState.Edit(list))
  }

  fun exitEditMode() = viewModelScope.launch {
    _selectedItems.clear()
    val list = produceUiListItem(items)
    _uiState.emit(UiState.Success(list))
  }

  private suspend fun produceUiListItem(
    data: List<Item>,
    header: String? = null,
    footer: String? = null,
    edit: Boolean = false,
  ): List<ListItem> = withContext(Dispatchers.Default) {
    return@withContext buildList {
      add(ListItem(Constants.ViewType.Header, text = header ?: "List Header", isEditMode = edit))
      for (item in data) {
        add(ListItem(Constants.ViewType.Card, item, isEditMode = edit))
      }
      if (data.size <= 30) {
        add(ListItem(Constants.ViewType.LoadMore, isEditMode = edit))
      } else {
        add(ListItem(Constants.ViewType.Footer, text = footer ?: "Footer here", isEditMode = edit))
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
    val data = listItem.data ?: return@launch

    if (checked) _selectedItems += data
    else _selectedItems -= data

    val header = "Selected: ${selectedItems.size} items"
    val list = produceUiListItem(items, header, edit = true)
      .map { item ->
        val isChecked = selectedItems.contains(item.data)
        item.copy(isChecked = isChecked)
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
