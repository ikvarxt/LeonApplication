package com.example.leonapplication.jsonlib.deserialize

import com.example.leonapplication.jsonlib.asJavaClass
import com.example.leonapplication.jsonlib.isPrimitiveOrString
import com.example.leonapplication.jsonlib.serializerForBasicType
import java.io.Reader
import java.io.StringReader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

inline fun <reified T : Any> deserialize(json: String): T {
  return deserialize(StringReader(json))
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> deserialize(json: Reader): T {
  val seed = ClassCacheHolder.createSeedFor(typeOf<T>().javaType)
  Parser(json, seed).parse()
  return seed.spawn() as T
}

interface JsonObject {

  fun setSimpleProperty(propName: String, value: Any?)

  fun createObject(propName: String): JsonObject
  fun createArray(propName: String): JsonObject
}

interface IClassCacheHolder {
  val classInfoCache: ClassInfoCache
}

interface Seed : JsonObject, IClassCacheHolder {

  fun spawn(): Any?
  fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject

  override fun createArray(propName: String): JsonObject = createCompositeProperty(propName, true)
  override fun createObject(propName: String): JsonObject = createCompositeProperty(propName, false)
}

object ClassCacheHolder : IClassCacheHolder {
  override val classInfoCache: ClassInfoCache = ClassInfoCache()
}

fun IClassCacheHolder.createSeedFor(paramType: Type, isList: Boolean? = null): Seed {
  val paramClass = paramType.asJavaClass()

  if (List::class.java.isAssignableFrom(paramClass)) {
    if (isList?.not() == true) throw JKidException("An array expected, not a composite object")
    val parameterizedType = paramType as? ParameterizedType
      ?: throw UnsupportedOperationException("Unsupported parameter type $this")

    val elementType = parameterizedType.actualTypeArguments.single()
    if (elementType.isPrimitiveOrString()) {
      return ValueListSeed(elementType, classInfoCache)
    }
    return ObjectListSeed(elementType, classInfoCache)
  }
  if (isList == true) throw JKidException("Object of the type ${paramType.typeName} expected, not an array")
  return ObjectSeed(paramClass.kotlin, classInfoCache)
}

class ObjectSeed<out T : Any>(
  targetClass: KClass<T>,
  override val classInfoCache: ClassInfoCache,
) : Seed {

  private val classInfo: ClassInfo<T> = classInfoCache[targetClass]

  private val valueParameters = mutableMapOf<KParameter, Any?>()
  private val seedParameters = mutableMapOf<KParameter, Seed>()

  private val arguments: Map<KParameter, Any?>
    get() = valueParameters + seedParameters.mapValues { it.value.spawn() }

  override fun spawn(): T = classInfo.createInstance(arguments)

  @OptIn(ExperimentalStdlibApi::class)
  override fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject {
    val param = classInfo.getConstructorParameter(propertyName)
    val deserializeAs = classInfo.getDeserializeClass(propertyName)
    val seed = createSeedFor(deserializeAs ?: param.type.javaType, isList)
    return seed.apply { seedParameters[param] = this }
  }

  override fun setSimpleProperty(propName: String, value: Any?) {
    val param = classInfo.getConstructorParameter(propName)
    valueParameters[param] = classInfo.deserializeConstructorArgument(param, value)
  }
}

class ObjectListSeed(
  private val elementType: Type,
  override val classInfoCache: ClassInfoCache,
) : Seed {

  private val elements = mutableListOf<Seed>()

  override fun setSimpleProperty(propName: String, value: Any?) {
    throw JKidException("Found primitive value in collection of object types")
  }

  override fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject {
    return createSeedFor(elementType, isList).also { elements.add(it) }
  }

  override fun spawn(): List<*> = elements.map { it.spawn() }
}

class ValueListSeed(
  elementType: Type,
  override val classInfoCache: ClassInfoCache,
) : Seed {

  private val elements = mutableListOf<Any?>()
  private val serializerForType = serializerForBasicType(elementType)

  override fun setSimpleProperty(propName: String, value: Any?) {
    elements.add(serializerForType.fromJsonValue(value))
  }

  override fun createCompositeProperty(propertyName: String, isList: Boolean): JsonObject {
    throw JKidException("Found object value in collection of primitive types")
  }

  override fun spawn() = elements
}