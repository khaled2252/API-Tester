package com.khaled.apitester.utils

import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */

object JSONObjectExtensions {
    fun JSONArray.toList(): List<Any> {
        val list = mutableListOf<Any>()
        val length = this.length()

        for (i in 0 until length) {
            val value = this.get(i)

            when (value) {
                is JSONObject -> list.add(value.toMap())
                is JSONArray -> list.add(value.toList())
                else -> list.add(value)
            }
        }

        return list
    }

    fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = this.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            val value = this.get(key)

            when (value) {
                is JSONObject -> map[key] = value.toMap()
                is JSONArray -> map[key] = value.toList()
                else -> map[key] = value
            }
        }

        return map
    }
}