package me.ikvarxt.jsviewdemo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.leonapplication.extension.button
import com.example.leonapplication.extension.list
import me.ikvarxt.leonapp.javetnodejs.NodeManager

class JsViewEntryActivity : AppCompatActivity() {

  private lateinit var nodeManager: NodeManager
  private lateinit var editText: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    nodeManager = NodeManager()
    nodeManager.init()
    setupView()

    val jsViewApi = JsViewApi(this, getSystemService(Context.WINDOW_SERVICE) as WindowManager)
    nodeManager.bindJvmClass("JsViewApi", jsViewApi)
  }

  private fun execute() {
//    nodeManager.restartWith(editText.text.toString())
    nodeManager.loadContext(editText.text.toString())
    nodeManager.call("main")
  }

  @SuppressLint("SetTextI18n")
  private fun setupView() {
    list {
      EditText(context).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
          .apply {
            weight = 1f
          }
        gravity = Gravity.TOP
        setText(
          """
          function main() {
            console.log(`Hello, World!`);
            JsViewApi.createView('1', "", 300, 300);
            
            setTimeout(() => {
              JsViewApi.move('1', 100, 100);
              console.log(`move to 100, 100`);
            }, 3000);
            console.log('call main');
          }
          """.trimIndent()
        )
      }.also { editText = it }
        .let { addView(it) }

      button("apply script") {
        execute()
      }
    }
  }
}