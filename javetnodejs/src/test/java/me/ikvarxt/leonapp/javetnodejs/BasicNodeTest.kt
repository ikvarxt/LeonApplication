package me.ikvarxt.leonapp.javetnodejs

import com.caoccao.javet.interop.converters.JavetProxyConverter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BasicNodeTest : StringSpec({

  lateinit var node: NodeManager

  beforeSpec {
    node = NodeManager()
    node.init()
    node.node.converter = JavetProxyConverter()
  }

  afterSpec {
    node.close()
  }

  "created node runtime" {
    node.node.isDead shouldBe false
  }

  "test 1 + 1" {
    node.node.getExecutor("1 + 1").executeInteger() shouldBe 2
  }

  "test bundle js" {
    val script = """
      (() => {
        "use strict"; 
        const o = function (o) {
          for (var t = "", n = 0; n < 11; n++)
            t += "%" + ("00" + o.charCodeAt(n).toString(16)).slice(-2);
          return t
        }("hello world"); 
        console.log(`Encoded string: ${'$'}{o}`), NativeCallback.nativeCallback({ a: "a" })
      })();
    """.trimIndent()
    node.node.globalObject.set("NativeCallback", NativeCallback::class.java)
    node.node.getExecutor(script).executeVoid()
  }

  "test inject java class" {
    node.node.apply {
      globalObject.set("StringBuilder", StringBuilder::class.java)
      val str = getExecutor(
        """
          function main() {
            return new StringBuilder('Hello').append(' from StringBuilder').toString();
          }
          main();
        """
      ).executeString()
      str shouldBe "Hello from StringBuilder"
      globalObject.delete("StringBuilder")
      globalObject.has("StringBuilder") shouldBe false
    }
  }

})
