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