package com.example.leonapplication.recyclerviewdemo

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar

class StatefulView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

  private lateinit var stateImage: ImageView
  private lateinit var stateText: TextView
  private lateinit var stateLoading: ContentLoadingProgressBar
  private lateinit var infoGroup: Group

  override fun onFinishInflate() {
    super.onFinishInflate()
    stateImage = findViewById(R.id.stateImage)
    stateText = findViewById(R.id.stateText)
    stateLoading = findViewById(R.id.stateLoading)
    infoGroup = findViewById(R.id.infoGroup)
  }

  fun setLoading() {
    infoGroup.isVisible = false
    stateLoading.show()
  }

  fun setError(error: Constants.Error) {
    stateLoading.hide()
    infoGroup.isVisible = true
    val (imgResId, textResId) = when (error) {
      Constants.Error.NoInternet -> R.drawable.placeholder to R.string.no_internet
      Constants.Error.NotLogin -> R.drawable.placeholder to R.string.not_login
      Constants.Error.LoadingFailed -> R.drawable.placeholder to R.string.loading_failed
      Constants.Error.InternalError -> R.drawable.placeholder to R.string.internal_error
    }
    stateImage.setImageResource(imgResId)
    stateText.setText(textResId)
  }
}