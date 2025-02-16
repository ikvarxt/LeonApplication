package com.example.leonapplication.recyclerviewdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.leonapplication.extension.fitsSystemBar
import kotlinx.coroutines.launch

class RecyclerViewActivity : AppCompatActivity(), CardListAdapter.ItemListener {

  private val viewModel by viewModels<ViewModel>()

  private lateinit var recyclerView: RecyclerView
  private lateinit var stateView: StatefulView
  private lateinit var listAdapter: CardListAdapter

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
    listAdapter = CardListAdapter(this)
    recyclerView.adapter = listAdapter
    val layoutManager = CardListAdapter.layoutManager(this) { pos ->
      listAdapter.getItemViewType(pos)
    }
    recyclerView.addItemDecoration(GridItemDecoration(layoutManager))
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

  override fun onClick(item: ListItem) {
    when (item.viewType) {
      Constants.ViewType.Card -> {
        val data = item.data ?: return
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

  override fun onLongClick(item: ListItem): Boolean {
    if (item.viewType == Constants.ViewType.Card) {
      toast("Long click card ${item.data?.id}")
      return true
    } else {
      return false
    }
  }

  private var toast: Toast? = null
  private fun toast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast?.show()
  }
}