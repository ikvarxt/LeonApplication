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
  private lateinit var jsViewApi: JsViewApi
  private lateinit var windowManager: WindowManager

  private val script = """
    function onClick(id) {
      console.log('on click: ' + id)
      JsViewApi.toast('click ' + id);
    }
    function main() {
      let id = JsViewApi.createView("", true, 100, 100);
      let layoutId = JsViewApi.createViewLayout(500, 300, [id]);
      JsViewApi.setBackgroundImage(layoutId, "");
      JsViewApi.showView(layoutId);
      JsViewApi.move(layoutId, 200, 200);
      
      JsViewApi.setOnClickListener(layoutId, "onClick");
    }
  """.trimIndent()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    nodeManager = NodeManager()
    nodeManager.init()
    setupView()

    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
  }

  private fun execute() {
    nodeManager.restartWith(editText.text.toString())
    jsViewApi = JsViewApi(this, windowManager, nodeManager.node)
    nodeManager.bindJvmClass("JsViewApi", jsViewApi)
    nodeManager.call("main")
  }

  @SuppressLint("SetTextI18n")
  private fun setupView() {
    list {
      EditText(context).apply {
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        lp.weight = 1f
        layoutParams = lp
        gravity = Gravity.TOP
        setText(script)
      }.also { editText = it }
        .let { addView(it) }

      button("apply script") {
        execute()
      }
    }
  }
}