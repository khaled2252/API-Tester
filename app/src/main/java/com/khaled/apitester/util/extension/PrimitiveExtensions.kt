package com.khaled.apitester.util.extension


fun String?.toNullIfBlank() = if (isNullOrBlank()) null else this

fun <K, V> Map<K, V>.toPrettyString(): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append("{\n")
    for ((key, value) in this) {
        stringBuilder.append("\t").append(key.toString()).append(" = ")
            .append(value.toString().removePrefix("[").removeSuffix("]")).append(",\n")
    }
    stringBuilder.append("}")
    return stringBuilder.toString()
}

fun String.toMap() : Map<String?, String>? = // Get Map of key/value pairs from a string where each line is a pair and each pair is separated by a space
    if (isNotEmpty()) split("\n").associate {
        if (it.contains(" ")) {
            val (key, value) = it.split(" ", limit = 2)
            key to value
        } else it to ""
    } else null