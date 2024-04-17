package com.example.leonapplication.extension

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView

internal class AssetsImageGetter(
  private val context: Context,
  private val pathPrefix: String = "",
) : Html.ImageGetter {
  override fun getDrawable(source: String?): Drawable? {
    source ?: return null
    return try {
      context.assets.open("$pathPrefix$source").use {
        BitmapFactory.decodeStream(it)
      }.let { BitmapDrawable(it) }
    } catch (e: Exception) {
      null
    }
  }
}

fun TextView.setTextWithImage(text: String) {
  if (text.contains("<img").not()) {
    setText(text)
    return
  }
  val imageGetter = AssetsImageGetter(context, "img/")
  val span = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
  val ssb = SpannableStringBuilder(span)
//  span.getSpans(0, span.length, ImageSpan::class.java).forEach { imageSpan ->
//    val customImageSpan = CustomImageSpan(imageSpan.drawable)
//    ssb.setSpan(
//      customImageSpan,
//      span.getSpanStart(imageSpan),
//      span.getSpanEnd(imageSpan),
//      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//    )
//  }
  setText(ssb)
}

class CustomImageSpan(
  private val drawable: Drawable,
) : ImageSpan(drawable, CUSTOM_ALIGN_BASELINE) {

  override fun draw(
    canvas: Canvas,
    text: CharSequence?,
    start: Int,
    end: Int,
    x: Float,
    top: Int,
    y: Int,
    bottom: Int,
    paint: Paint,
  ) {
    if (mVerticalAlignment != CUSTOM_ALIGN_BASELINE) {
      super.draw(canvas, text, start, end, x, top, y, bottom, paint)
      return
    }

    canvas.save()
    val size = bottom - top
    drawable.setBounds(0, 0, size, size)

    val fm = paint.fontMetricsInt
//    val transY =  + (bottom - top) / 2 - b.getBounds().height() / 2
    val transY = bottom - drawable.bounds.bottom

    canvas.translate(x, top.toFloat())
    drawable.draw(canvas)
    canvas.restore()
  }

  companion object {
    const val CUSTOM_ALIGN_BASELINE = 110
  }
}