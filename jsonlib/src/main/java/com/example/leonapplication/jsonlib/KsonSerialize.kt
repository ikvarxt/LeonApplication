package com.example.leonapplication.jsonlib

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

fun serialize(obj: Any): String = buildString { serializeObject(obj) }

private const val INDICATOR = ":"
private const val SEPARATOR = ","
private const val OBJECT_START = "{"
private const val OBJECT_ENT = "}"
private const val ARRAY_START = "["
private const val ARRAY_END = "]"
private const val NULL_VAL = "null"

private val classCache = hashMapOf<KClass<*>, Collection<KProperty1<Any, *>>>()

private fun StringBuilder.serializeObject(obj: Any) {
  if (obj is List<*>) {
    serializeList(obj)
    return
  }
  val kClass = obj.javaClass.kotlin
  val members = classCache.getOrPut(kClass) {
    kClass.sortedMemberProperties
  }.filter { it.findAnnotation<JsonExclude>() == null }
  members.joinToStringBuilder(
    this,
    separator = SEPARATOR,
    prefix = OBJECT_START,
    postfix = OBJECT_ENT
  ) { prop ->
    serializeString(prop.name)
    append(INDICATOR)
    serializePropertyValue(prop.call(obj))
  }
}

internal fun KProperty<*>.getSerializer(): ValueSerializer<Any?>? {
  val customSerializerAnn = findAnnotation<CustomSerializer>() ?: return null
  val serializerClass = customSerializerAnn.serializerClass

  val valueSerializer = serializerClass.objectInstance
    ?: serializerClass.createInstance()
  @Suppress("UNCHECKED_CAST")
  return valueSerializer as ValueSerializer<Any?>
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
