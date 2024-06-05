package me.ikvarxt.leonapp.javetnodejs

import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.converters.JavetProxyConverter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BasicNodeTest : StringSpec({

  lateinit var node: NodeManager

  beforeAny {
    node = NodeManager()
    node.init()
    node.node.converter = JavetProxyConverter()
  }

  afterAny {
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

  "bind a class instanced from native" {
    val runtime = node.node
    val bindNode = BindNode("bind test")
    runtime.createV8ValueObject().use { v8Obj ->
      runtime.globalObject.set("BindNode", v8Obj)
      v8Obj.bind(bindNode)
    }
    val script = """
      BindNode.test(" js")
    """.trimIndent()
    runtime.getExecutor(script).executeString() shouldBe "bind test js"
  }

  "bind a class instanced from native called in function" {
    val runtime = node.node
    val bindNode = BindNode("bind test")
    runtime.createV8ValueObject().use { v8Obj ->
      runtime.globalObject.set("BindNode", v8Obj)
      v8Obj.bind(bindNode)
    }
    val script = """
      function main(str) {
        return BindNode.test(" js")
      }
    """.trimIndent()
    runtime.getExecutor(script).executeVoid()
    runtime.globalObject.invokeString("main") shouldBe "bind test js"
  }

}) {


  class BindNode(val name: String) {

    @V8Function
    fun test(s: String): String {
      return name + s
    }
  }
}
