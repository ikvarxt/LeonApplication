package com.example.leonapplication.recyclerviewdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.leonapplication.extension.fitsSystemBar
import kotlinx.coroutines.launch

class RecyclerViewActivity : AppCompatActivity(), CardListAdapter.ItemListener {

  private val viewModel by viewModels<ViewModel>()

  private lateinit var recyclerView: RecyclerView
  private lateinit var stateView: StatefulView
  private lateinit var listAdapter: CardListAdapter
  private lateinit var statusBarGradient: View

  private val backPressedCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() {
      if (viewModel.isEditMode) {
        editMode(false)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initViews()
    initRecycler()

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
          viewModel.uiState.collect(::onUiState)
        }
      }
    }
    viewModel.requestFirstPage()

    onBackPressedDispatcher.addCallback(this, backPressedCallback)
  }

  private fun initViews() {
    enableEdgeToEdge()
    setContentView(R.layout.activity_recycler_view)
    statusBarGradient = findViewById(R.id.status_bar_gradient)
    recyclerView = findViewById(R.id.recyclerView)
    recyclerView.fitsSystemBar()
    stateView = findViewById(R.id.stateView)
    FadeViewScrollHelper.initialHeight(statusBarGradient)
    val scrollHeight = resources.getDimensionPixelSize(R.dimen.standard_scroll_height)
    recyclerView.addOnScrollListener(FadeViewScrollHelper(statusBarGradient, scrollHeight))
  }

  private fun initRecycler() {
    listAdapter = CardListAdapter(this)
    recyclerView.adapter = listAdapter
    recyclerView.addItemDecoration(GridItemDecoration(this))
    val layoutManager = CardListAdapter.layoutManager(this, ::viewTypeGetter)
    recyclerView.layoutManager = layoutManager
  }

  private fun onUiState(state: UiState) {
    if (state is UiState.Success || state is UiState.Edit) {
      recyclerView.isVisible = true
      stateView.isVisible = false
    } else {
      recyclerView.isVisible = false
      stateView.isVisible = true
    }

    when (state) {
      UiState.Loading -> stateView.setLoading()
      is UiState.Error -> stateView.setError(state.error)
      is UiState.Success -> listAdapter.submitList(state.items)
      is UiState.Edit -> listAdapter.submitList(state.items)
    }
  }

  override fun onClick(listItem: ListItem) {
    when (listItem.viewType) {
      Constants.ViewType.Card -> {
        val data = listItem.data ?: return
        toast("Card ${data.id} Clicked")
        Intent(Intent.ACTION_VIEW).apply {
          setData(data.imgUrl.toUri())
        }.also(::startActivity)
      }

      Constants.ViewType.Header -> toast("Header Clicked")
      Constants.ViewType.LoadMore -> viewModel.loadMore()
      Constants.ViewType.Footer -> toast("Footer Clicked")
    }
  }

  override fun onLongClick(listItem: ListItem): Boolean {
    if (listItem.viewType == Constants.ViewType.Card) {
      editMode(true)
      onChecked(listItem, true)
      return true
    } else {
      return false
    }
  }

  override fun onChecked(listItem: ListItem, checked: Boolean) {
    viewModel.onChecked(listItem, checked)
  }

  private fun viewTypeGetter(pos: Int) = listAdapter.getItemViewType(pos)

  private fun editMode(isEdit: Boolean) {
    if (isEdit) {
      recyclerView.itemAnimator = null
      viewModel.enterEditMode()
    } else {
      viewModel.exitEditMode()
      recyclerView.itemAnimator = DefaultItemAnimator()
    }
    backPressedCallback.isEnabled = isEdit
  }

  private var toast: Toast? = null
  private fun toast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast?.show()
  }
}
