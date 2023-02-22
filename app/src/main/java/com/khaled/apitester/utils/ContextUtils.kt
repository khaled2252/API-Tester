package com.khaled.apitester.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.khaled.apitester.ApiCallModel
import com.khaled.apitester.utils.JSONObjectExtensions.toMap
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */
object ContextUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null // Capabilities (TRANSPORT_CELLULAR, TRANSPORT_WIFI, TRANSPORT_ETHERNET) will be null if there is no network
    }

    class SharedPrefs(context: Context) {
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE)

        fun saveApiCallModel(apiCallModel : ApiCallModel) {
            val json = JSONObject()
            json.put("dateInMillis", apiCallModel.dateInMillis)
            json.put("requestUrl", apiCallModel.requestUrl)
            json.put("requestMethod", apiCallModel.requestMethod.name)
            apiCallModel.requestBody?.let { json.put("requestBody", it) }
            apiCallModel.requestHeaders?.let { json.put("requestHeaders", JSONObject(it.filterKeys { it != null })) }
            apiCallModel.requestFile?.let { json.put("requestFile", it.path) }
            apiCallModel.requestQueries?.let { json.put("requestQueries", JSONObject(it.filterKeys { it != null })) }
            apiCallModel.responseCode?.let { json.put("responseCode", it) }
            apiCallModel.responseMessage?.let { json.put("responseMessage", it) }
            apiCallModel.responseHeaders?.let { json.put("responseHeaders", JSONObject(it.filterKeys { it != null })) }
            apiCallModel.responseBody?.let { json.put("responseBody", it) }
            apiCallModel.responseError?.let { json.put("responseError", it) }

            val previousApiCallsString : String = sharedPreferences.getString("apiCalls", "[]") ?: "[]"
            val previousApiCalls = JSONArray(previousApiCallsString)
            previousApiCalls.put(json)

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

                    val dateInMillis = jsonObject.getInt("dateInMillis")
                    val requestUrl = jsonObject.getString("requestUrl")
                    val requestMethod = HttpUtils.HttpMethod.valueOf(jsonObject.getString("requestMethod"))
                    val requestHeaders = jsonObject.optJSONObject("requestHeaders")?.toMap() as Map<String?, String>?
                    val requestBody = jsonObject.optJSONObject("requestBody")
                    val requestFile = File(jsonObject.optString("requestFile", null.toString()))
                    val requestQueries = jsonObject.optJSONObject("requestQueries")?.toMap() as Map<String?, String>?
                    val responseCode = jsonObject.optInt("responseCode", -1).takeIf { it != -1 }
                    val responseMessage = jsonObject.optString("responseMessage", null)
                    val responseHeaders = jsonObject.optJSONObject("responseHeaders")?.toMap() as Map<String?, List<String>>?
                    val responseBody = jsonObject.optString("responseBody", null)
                    val responseError = jsonObject.optString("responseError", null)

                    val apiCall = ApiCallModel(dateInMillis, requestUrl, requestMethod, requestHeaders, requestBody, requestFile, requestQueries, responseCode, responseMessage, responseHeaders, responseBody, responseError)
                    apiCallList.add(apiCall)
                }

                return apiCallList
            } else {
                return emptyList()
            }
        }

    }

}