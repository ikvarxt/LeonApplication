package com.example.leonapplication.jsonlib

data class PersonBean(val name: String, val age: Int)

data class StringBean(val s: String)
data class IntBean(val i: Int)
data class LongBean(val l: Long)
data class DoubleBean(val d: Double, val f: Float)
data class NullableBean(val n: Any?)
data class ListBean(val l: List<String>)
data class ObjectBean(val o: StringBean)

data class ExcludeBean(@JsonExclude val e: String)