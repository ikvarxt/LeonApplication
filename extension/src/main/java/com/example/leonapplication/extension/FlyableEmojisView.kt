package com.example.leonapplication.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random


class FlyableEmojisView(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

  init {
    val drawable = try {
      context.assets.open("img/emoji_kiss.png")
        .let { BitmapFactory.decodeStream(it) }
        .let { BitmapDrawable(context.resources, it) }
    } catch (e: IOException) {
      throw IllegalArgumentException("not found emoji drawable from assets")
    }

    fun HeartAnimationView.pop() {
      burstHearts(
        drawable,
        size = (70 to 70),
        start = (width / 2f to height / 2f),
        end = (width / 4f to height - context.resources.displayMetrics.density * 40),
        count = 6
      )
    }

    val heartAnimationView = HeartAnimationView(context)
    addView(heartAnimationView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

    setOnClickListener {
      heartAnimationView.cancel()
      heartAnimationView.pop()
    }

    heartAnimationView.post { heartAnimationView.pop() }
  }

}

class HeartAnimationView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

  private val hearts = mutableListOf<Heart>()
  private val animators = CopyOnWriteArrayList<Animator>()
  var animationDuration = 1000L

  fun burstHearts(
    drawable: Drawable,
    size: Pair<Int, Int>,
    start: Pair<Float, Float>,
    end: Pair<Float, Float>,
    count: Int,
    delayBetweenHearts: Long = 50L, // 心形之间的延迟时间
  ) {

    val addHeartsAction = Runnable {
      val (startX: Float, startY: Float) = start
      val (endX: Float, endY: Float) = end
      val (width, height) = size

      Heart(drawable, width, height, startX, startY, endX, endY).also {
        // 先绘制后添加的动画
        hearts.add(0, it)
        startHeartAnimation(it, animationDuration)
      }
    }

    val handler = Handler(Looper.getMainLooper())
    repeat(count) { index ->
      val delayTime = index * delayBetweenHearts
      handler.postDelayed(addHeartsAction, delayTime)
    }
  }

  fun cancel() {
    animators.forEach { it.cancel() }
  }

  private fun startHeartAnimation(heart: Heart, heartAnimationDuration: Long) {
    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
      duration = heartAnimationDuration
      interpolator = AccelerateDecelerateInterpolator()

      addUpdateListener { animator ->
        heart.progress = animator.animatedValue as Float
        invalidate()
      }

      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          hearts -= heart
          animators -= animation
        }
      })

      start()
    }
    animators += animator
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    hearts.forEach { heart ->
      canvas.save()

      val x = calculateBezierPoint(heart.startX, heart.controlX, heart.endX, heart.progress)
      val y = calculateBezierPoint(heart.startY, heart.controlY, heart.endY, heart.progress)

      canvas.translate(x, y)

      // 缩放效果：开始时略大，然后逐渐变小
      val scale = 1.2f - 0.7f * heart.progress
      canvas.scale(scale, scale)

      heart.drawable.draw(canvas)
      canvas.restore()
    }
  }

  companion object {
    private const val CONTROL_POINT_SPREAD = 80f // 控制点扩散范围

    /**
     * 二阶贝塞尔曲线计算公式
     */
    private fun calculateBezierPoint(start: Float, control: Float, end: Float, t: Float): Float {
      return (1 - t) * (1 - t) * start + 2 * (1 - t) * t * control + t * t * end
    }
  }


  class Heart(
    val drawable: Drawable,
    val width: Int, val height: Int,
    val startX: Float, val startY: Float,
    val endX: Float, val endY: Float,
  ) {

    var progress: Float = 0f

    // 随机生成控制点，但保持在起点和终点之间的区域内
    val controlX: Float = randomControl(startX, endX)
    val controlY: Float = randomControl(startX, endY)

    init {
      drawable.setBounds(0, 0, width, height)
    }

    private fun randomControl(start: Float, end: Float) = (start + end) / 2 +
      (Random.nextFloat() * 2 - 1) * CONTROL_POINT_SPREAD
  }
}