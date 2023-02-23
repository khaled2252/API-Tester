package com.khaled.apitester.util

import android.content.Context
import android.content.SharedPreferences
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.extension.JSONObjectExtensions.toMap
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SharedPrefsUtils(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE)

    fun addApiCallModel(apiCallModel: ApiCallModel) {
        val json = JSONObject()
        json.put("dateInMillis", apiCallModel.dateInMillis)
        json.put("requestUrl", apiCallModel.requestUrl)
        json.put("requestMethod", apiCallModel.requestMethod.name)
        apiCallModel.requestBody?.let { json.put("requestBody", it) }
        apiCallModel.requestHeaders?.let {
            json.put(
                "requestHeaders",
                JSONObject(it.filterKeys { it != null })
            )
        }
        apiCallModel.requestFile?.let { json.put("requestFile", it.path) }
        apiCallModel.requestQueries?.let {
            json.put(
                "requestQueries",
                JSONObject(it.filterKeys { it != null })
            )
        }
        apiCallModel.responseCode?.let { json.put("responseCode", it) }
        apiCallModel.responseMessage?.let { json.put("responseMessage", it) }
        apiCallModel.responseHeaders?.let {
            json.put(
                "responseHeaders",
                JSONObject(it.filterKeys { it != null })
            )
        }
        apiCallModel.responseBody?.let { json.put("responseBody", it) }
        apiCallModel.responseError?.let { json.put("responseError", it) }
        apiCallModel.executionTime?.let { json.put("executionTime", it) }

        fun JSONArray.addToPos(pos: Int, jsonObj: JSONObject) {
            for (i in length() downTo pos + 1) {
                put(i, get(i - 1))
            }
            put(pos, jsonObj)
        }

        val previousApiCallsString: String =
            sharedPreferences.getString("apiCalls", "[]") ?: "[]"
        val previousApiCalls = JSONArray(previousApiCallsString)
        previousApiCalls.addToPos(0, json)

        val editor = sharedPreferences.edit()
        editor.putString("apiCalls", previousApiCalls.toString())
        editor.apply()
    }

    fun getPreviousApiCalls(): List<ApiCallModel> {

        val apiCallListJson = sharedPreferences.getString("apiCalls", null)

        if (apiCallListJson != null) {
            val apiCallList = mutableListOf<ApiCallModel>()
            val jsonArray = JSONArray(apiCallListJson)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val dateInMillis = jsonObject.getLong("dateInMillis")
                val requestUrl = jsonObject.getString("requestUrl")
                val requestMethod =
                    HttpUtils.HttpMethod.valueOf(jsonObject.getString("requestMethod"))
                val requestHeaders =
                    jsonObject.optJSONObject("requestHeaders")?.toMap() as Map<String?, String>?
                val requestBody = jsonObject.optString("requestBody", null.toString())
                val requestFile = File(jsonObject.optString("requestFile", null.toString()))
                val requestQueries =
                    jsonObject.optJSONObject("requestQueries")?.toMap() as Map<String?, String>?
                val responseCode = jsonObject.optInt("responseCode", -1).takeIf { it != -1 }
                val responseMessage = jsonObject.optString("responseMessage", null.toString())
                val responseHeaders = jsonObject.optJSONObject("responseHeaders")
                    ?.toMap() as Map<String?, List<String>>?
                val responseBody = jsonObject.optString("responseBody", null.toString())
                val responseError = jsonObject.optString("responseError", null.toString())
                val executionTime = jsonObject.optLong("executionTime", 0L)

                val apiCall = ApiCallModel(
                    dateInMillis,
                    requestUrl,
                    requestMethod,
                    requestHeaders,
                    requestBody,
                    requestFile,
                    requestQueries,
                    responseCode,
                    responseMessage,
                    responseHeaders,
                    responseBody,
                    responseError,
                    executionTime
                )
                apiCallList.add(apiCall)
            }

            return apiCallList
        } else {
            return emptyList()
        }
    }

    fun getApiCallModel(apiCallModelDateIdentifier: Long): ApiCallModel? {
        getPreviousApiCalls().forEach {
            if (it.dateInMillis == apiCallModelDateIdentifier) {
                return it
            }
        }
        return null
    }

}