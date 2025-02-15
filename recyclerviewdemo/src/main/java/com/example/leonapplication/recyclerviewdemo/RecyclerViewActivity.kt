package com.example.leonapplication.recyclerviewdemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.leonapplication.extension.fitsSystemBar
import kotlinx.coroutines.launch

class RecyclerViewActivity : AppCompatActivity() {

  private val viewModel by viewModels<ViewModel>()

  private lateinit var recyclerView: RecyclerView
  private lateinit var stateView: StatefulView
  private lateinit var listAdapter: ListAdapter

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
  }

  private fun initViews() {
    enableEdgeToEdge()
    setContentView(R.layout.activity_recycler_view)
    recyclerView = findViewById(R.id.recyclerView)
    recyclerView.fitsSystemBar()
    stateView = findViewById(R.id.stateView)
  }

  private fun initRecycler() {
    listAdapter = ListAdapter()
    recyclerView.adapter = listAdapter
    val layoutManager = GridLayoutManager(this, 3).apply {
      spanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
          val isCard = listAdapter.getItemViewType(position) == Constants.ViewType.Card.type
          return if (isCard) 1 else 3
        }
      }
    }
    recyclerView.layoutManager = layoutManager
  }

  private fun onUiState(uiState: UiState) {
    if (uiState is UiState.Success) {
      recyclerView.isVisible = true
      stateView.isVisible = false
    } else {
      recyclerView.isVisible = false
      stateView.isVisible = true
    }

    when (uiState) {
      UiState.Loading -> stateView.setLoading()
      is UiState.Error -> stateView.setError(uiState.error)
      is UiState.Success -> listAdapter.submitList(uiState.items)
    }
  }

}