package com.example.leonapplication.jsonlib

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun serialize(obj: Any): String = buildString { serializeObject(obj) }

private const val INDICATOR = ":"
private const val SEPARATOR = ","
private const val OBJECT_START = "{"
private const val OBJECT_ENT = "}"
private const val ARRAY_START = "["
private const val ARRAY_END = "]"
private const val NULL_VAL = "null"

private val classCache = hashMapOf<KClass<*>, Collection<KProperty1<Any, *>>>()

private val <T : Any> KClass<T>.declaredMemberProperties: Collection<KProperty1<T, *>>
  get() = java.declaredFields.withIndex()
    .associate { it.value.name to it.index }
    .let { orderById ->
      memberProperties.sortedBy { orderById[it.name] }
    }

private fun StringBuilder.serializeObject(obj: Any) {
  if (obj is List<*>) {
    serializeList(obj)
    return
  }
  val kClass = obj.javaClass.kotlin
  val members = classCache[kClass]
    ?: kClass.declaredMemberProperties.also { classCache[kClass] = it }

  members.joinToStringBuilder(this, separator = SEPARATOR, prefix = OBJECT_START, postfix = OBJECT_ENT) { prop ->
    serializeString(prop.name)
    append(INDICATOR)
    serializePropertyValue(prop.call(obj))
  }
}

private fun StringBuilder.serializePropertyValue(obj: Any?) {
  when (obj) {
    null -> append(NULL_VAL)
    is String -> serializeString(obj)
    is Boolean, is Number -> append(obj.toString())
    is List<*> -> serializeList(obj)
    else -> serializeObject(obj)
  }
}

private fun StringBuilder.serializeList(obj: List<*>) {
  obj.joinToStringBuilder(
    this,
    separator = SEPARATOR,
    prefix = ARRAY_START,
    postfix = ARRAY_END
  ) {
    serializePropertyValue(it)
  }
}

private fun StringBuilder.serializeString(s: String) {
  append('\"')
  s.forEach { append(it.escape()) }
  append('\"')
}

private fun Char.escape() = when (this) {
  '\\' -> "\\\\"
  '\"' -> "\\\""
  '\b' -> "\\b"
  '\u000C' -> "\\f"
  '\n' -> "\\n"
  '\r' -> "\\r"
  '\t' -> "\\t"
  else -> this.toString()
}
