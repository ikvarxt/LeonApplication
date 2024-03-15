package com.example.leonapplication.jsonlib

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun <T> Iterable<T>.joinToStringBuilder(
  stringBuilder: StringBuilder,
  separator: CharSequence = ", ",
  prefix: CharSequence = "",
  postfix: CharSequence = "",
  limit: Int = -1,
  truncated: CharSequence = "...",
  callback: ((T) -> Unit)? = null,
): StringBuilder {
  return joinTo(stringBuilder, separator, prefix, postfix, limit, truncated) {
    if (callback == null) return@joinTo it.toString()
    callback(it)
    ""
  }
}


/**
 * get memberProperties of kClass with its declared order
 */
internal val <T : Any> KClass<T>.sortedMemberProperties: Collection<KProperty1<T, *>>
  get() = java.declaredFields.withIndex()
    .associate { it.value.name to it.index }
    .let { orderById ->
      memberProperties.sortedBy { orderById[it.name] }
    }

fun Type.isPrimitiveOrString(): Boolean {
  val cls = this as? Class<*> ?: return false
  return cls.kotlin.javaPrimitiveType != null || cls == String::class.java
}


@Suppress("UNCHECKED_CAST")
fun Type.asJavaClass(): Class<Any> = when (this) {
  is Class<*> -> this as Class<Any>
  is ParameterizedType -> rawType as? Class<Any>
    ?: throw UnsupportedOperationException("Unknown type $this")

  else -> throw UnsupportedOperationException("Unknown type $this")
}